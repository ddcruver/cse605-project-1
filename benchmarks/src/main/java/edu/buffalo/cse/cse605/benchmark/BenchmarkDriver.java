package edu.buffalo.cse.cse605.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 11:43 AM
 */
public class BenchmarkDriver
{
    private static final Logger LOG = LoggerFactory.getLogger(BenchmarkDriver.class);
    private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(1000);
    private static final long SPIN_YIELD_PRECISION = TimeUnit.MILLISECONDS.toNanos(100);
    private Writer rawOut;
    private Writer normalizedOut;

    private void writeRawResults(String testTitle, List<BenchmarkResult> benchmarkResults) throws IOException
    {
        int iteration = 1;
        for (BenchmarkResult result : benchmarkResults)
        {
            rawOut.write(testTitle);
            rawOut.write(",");
            rawOut.write(Integer.toString(iteration));
            iteration++;
            rawOut.write(",");
            rawOut.write(result.toCsv());
            rawOut.write("\n");
            rawOut.flush();
        }
    }

    private void writeNormalizedResults(String testTitle, List<BenchmarkResult> benchmarkResults) throws IOException
    {
        long iteration = 0;
        long averageCounts = 0;
        normalizedOut.write(testTitle);
        normalizedOut.write(",");

        long totalOperations;
        long totalReads = 0;
        long totalWrites = 0;
        long totalDeletes = 0;
        long totalErrors = 0;

        long skipFirst = 1;

        for (BenchmarkResult result : benchmarkResults)
        {
            if (iteration < skipFirst)
            {
                totalReads += result.getReads();
                totalWrites += result.getWrites();
                totalDeletes += result.getDeletes();
                totalErrors += result.getErrors();
                averageCounts++;
            }
            iteration++;
        }

        totalOperations = totalReads + totalWrites + totalDeletes;
        normalizedOut.write(Long.toString(totalOperations / averageCounts));
        normalizedOut.write(",");
        normalizedOut.write(Long.toString(totalReads / averageCounts));
        normalizedOut.write(",");
        normalizedOut.write(Long.toString(totalWrites / averageCounts));
        normalizedOut.write(",");
        normalizedOut.write(Long.toString(totalDeletes / averageCounts));
        normalizedOut.write(",");
        normalizedOut.write(Long.toString(totalErrors / averageCounts));
        normalizedOut.write("\n");
        normalizedOut.flush();
    }

    ;

    public void runIterations(final Benchmark benchmark, int secondsToRun, int threadPoolSize, int iterations) throws InterruptedException, IOException
    {
        List<BenchmarkResult> results = new ArrayList<BenchmarkResult>();
        for (int i = 0; i < iterations; i++)
        {
            LOG.info("Initialize Test #{} for {}", i + 1, benchmark.getTestName());
            results.add(runIteration(benchmark, secondsToRun, threadPoolSize));
        }

        StringBuilder resultsStringBuiler = new StringBuilder();
        for (BenchmarkResult result : results)
        {
            resultsStringBuiler.append(result.toCsv());
            resultsStringBuiler.append("\n");
        }

        LOG.info("{} Results:\n{}", benchmark.getTestName(), resultsStringBuiler);

        writeResults(benchmark.getTestName(), results);
    }

    public BenchmarkResult runIteration(final Benchmark benchmark, final int secondsToRun, final int threadPoolSize) throws InterruptedException
    {
        // Setup Thread Pool
        ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) threadPool;
        executor.setCorePoolSize(threadPoolSize);
        executor.prestartAllCoreThreads();

        warmUpThreadPool(executor, 1, 2);

        benchmark.initRun();

        // Just in case it will listen
        System.gc();

        final Semaphore running = new Semaphore(0);
        final Semaphore threadsReady = new Semaphore(0);
        final Semaphore canBegin = new Semaphore(0);

        for (int i = 0; i < threadPoolSize; i++)
        {
            final int threadNumber = i;
            executor.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        benchmark.initThread(threadNumber);
                    } catch (InterruptedException ex)
                    {
                        LOG.error("Was interrupted while initializing thread.", ex);
                    }

                    threadsReady.release();
                    try
                    {
                        canBegin.acquire();
                        benchmark.run(threadNumber);
                    } catch (InterruptedException ex)
                    {
                        LOG.error("Was interrupted while waiting for beginning of run.", ex);
                    } finally
                    {
                        running.release();
                    }


                }
            });
        }

        LOG.info("Waiting for {} Threads to Initialize", threadPoolSize);
        threadsReady.acquire(threadPoolSize);
        LOG.info("Starting Threads");
        canBegin.release(threadPoolSize);

        LOG.info("Letting threads run for {} seconds", secondsToRun);
        long startTime = System.nanoTime();

        sleepNanos(TimeUnit.SECONDS.toNanos(secondsToRun));

        LOG.debug("Set Run State to False");
        benchmark.setRunning(false);

        running.acquire(threadPoolSize);

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        LOG.info("Ran for {} seconds ({} ms)", TimeUnit.NANOSECONDS.toSeconds(elapsedTime), TimeUnit.NANOSECONDS.toMillis(elapsedTime));

        LOG.info("All Running Threads were Terminated");

        LOG.info("Ending Test");

        BenchmarkResult result = new BenchmarkResult(benchmark);

        LOG.debug("Cleaning up Test");
        shutdownThreadPoolExecutor(executor);

        // Just in case it will listen
        System.gc();
        return result;
    }

    private static long warmUpThreadPool(ThreadPoolExecutor executor, final int threadPerCoreThreads, final int warmUpTimeSec)
    {
        final AtomicLong warmUpCounterThreads = new AtomicLong(0);
        final AtomicLong warmUpCounterSpins = new AtomicLong(0);

        final long warmUpTime = (long) Math.pow(10, 9) * warmUpTimeSec;
        final long warmUpThreads = executor.getPoolSize() * threadPerCoreThreads;

        LOG.debug("Warm Up Time: {} ms", TimeUnit.NANOSECONDS.toMillis(warmUpTime));

        for (int i = 0; i < warmUpThreads; i++)
        {
            executor.submit(new Runnable()
            {
                @Override
                public void run()
                {

                    long startTime = System.nanoTime();
                    long endTime = startTime + warmUpTime;

                    warmUpCounterThreads.incrementAndGet();

                    long currentTime = 0;
                    while (currentTime <= endTime)
                    {
                        currentTime = System.nanoTime();
                        warmUpCounterSpins.incrementAndGet();
                    }
                }
            });
        }

        long completedTasks = -1;
        while (completedTasks < warmUpThreads)
        {
            completedTasks = executor.getCompletedTaskCount();
        }

        LOG.debug("Warm Up Done; counterThreads={} spins={}", warmUpCounterThreads, warmUpCounterSpins);

        return completedTasks;
    }

    public void shutdownThreadPoolExecutor(ThreadPoolExecutor executor)
    {
        executor.shutdown();

        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {
            LOG.error("Thread was interrupted when trying to shutdown thread pool.", e);
        }
        executor.shutdownNow();
    }


    public static Double getRandomDouble()
    {
        return Math.random();
    }

    public static Double getRandomDouble(double max)
    {
        return Math.random() * max;
    }

    public static void sleepNanos(long nanoDuration) throws InterruptedException
    {
        final long end = System.nanoTime() + nanoDuration;
        long timeLeft = nanoDuration;
        do
        {
            if (timeLeft > SLEEP_PRECISION)
                Thread.sleep(1);
            //else if (timeLeft > SPIN_YIELD_PRECISION)
            //	Thread.yield();

            timeLeft = end - System.nanoTime();
        } while (timeLeft > 0);
    }

    private void writeHeader(int threads)
    {
        try
        {
            rawOut.append(",,");
            rawOut.append(Integer.toString(threads));
            rawOut.append("\n");
            rawOut.flush();
        } catch (IOException e)
        {
            LOG.error("Could write header to raw output file.", e);
        }

        try
        {
            normalizedOut.append(",");
            normalizedOut.append(Integer.toString(threads));
            normalizedOut.append("\n");
            normalizedOut.flush();
        } catch (IOException e)
        {
            LOG.error("Could write header to normalized output file.", e);
        }

    }

    protected static void closeFiles(Writer... writers)
    {
        for (Writer writer : writers)
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                } catch (IOException e)
                {
                    LOG.error("Could not close file", e);
                }
            }
        }
    }

    public void start(String testName, int threads, int iterations, int secondsToRun, long initialListSize) throws IOException
    {
        //startSleeperThread();
        rawOut = getRawResultWriter(testName, threads, iterations, secondsToRun, initialListSize);
        normalizedOut = getResultWriter(testName, threads, iterations, secondsToRun, initialListSize);

        writeHeader(threads);
    }

    private static String getBaseFileName(String testName, int threads, int iterations, int secondsToRun, long initialListSize)
    {
        return testName + "-" + threads + "t-" + iterations + "i-" + secondsToRun + "s-" + initialListSize + "ils";
    }

    private BufferedWriter getRawResultWriter(String testName, int threads, int iterations, int secondsToRun, long initialListSize) throws IOException
    {
        String filename = getBaseFileName(testName, threads, iterations, secondsToRun, initialListSize) + ".out-raw";

        // Delete existing report file if it already exists
        File file = new File(filename);
        if (file.exists())
            file.delete();

        FileWriter rawFileWriter = new FileWriter(filename);
        return new BufferedWriter(rawFileWriter);
    }

    private static BufferedWriter getResultWriter(String testName, int threads, int iterations, int secondsToRun, long initialListSize) throws IOException
    {
        String filename = getBaseFileName(testName, threads, iterations, secondsToRun, initialListSize) + ".out";
        FileWriter rawFileWriter = new FileWriter(filename);
        return new BufferedWriter(rawFileWriter);
    }

    private void writeResults(String testTitle, List<BenchmarkResult> benchmarkResults) throws IOException
    {
        writeRawResults(testTitle, benchmarkResults);
        writeNormalizedResults(testTitle, benchmarkResults);
    }

    public void stop()
    {
        closeFiles(rawOut, normalizedOut);
    }

    private static void startSleeperThread()
    {
        Runnable runner = new Runnable()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e)
                {
                    //Ignore
                }
            }
        };

        Thread t = new Thread(runner, "Sleeper");
        t.setDaemon(true);
        t.start();
    }
}
