package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
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

	public List<BenchmarkResult> runIterations(final Benchmark benchmark, int threadPoolSize, int iterations) throws InterruptedException
	{
		List<BenchmarkResult> results = new ArrayList<BenchmarkResult>();
		for (int i = 0; i < iterations; i++)
		{
			results.add(runIteration(benchmark, threadPoolSize));
		}

		StringBuilder resultsStringBuiler = new StringBuilder();
		for (BenchmarkResult result : results)
		{
			resultsStringBuiler.append(result.toCsv());
			resultsStringBuiler.append("\n");
		}

		LOG.info("Results:\n{}", resultsStringBuiler);

		return results;
	}

	public BenchmarkResult runIteration(final Benchmark benchmark, final int threadPoolSize) throws InterruptedException
	{
		// Setup Thread Pool
		ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);
		ThreadPoolExecutor executor = (ThreadPoolExecutor) threadPool;
		executor.prestartAllCoreThreads();
		executor.setCorePoolSize(threadPoolSize);

		long currentActiveCount = warmUpThreadPool(executor, 2, 2);

		benchmark.initRun();

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

		LOG.info("Waiting for {} Threads to Initialize", Long.valueOf(threadPoolSize));
		threadsReady.acquire(threadPoolSize);
		LOG.info("Starting Threads");
		canBegin.release(threadPoolSize);

		int secondsToRun = 5;

		LOG.info("Letting threads run for {} seconds", secondsToRun);
		Thread.sleep(5000);
		LOG.debug("Set Run State to False");
		benchmark.setRunning(false);
		running.acquire(threadPoolSize);

		LOG.info("All Running Threads were Terminated");

		LOG.info("Ending Test");

		BenchmarkResult result = new BenchmarkResult(benchmark);

		LOG.debug("Cleaning up Test");
		shutdownThreadPoolExecutor(executor);

		return result;
	}

	private static long warmUpThreadPool(ThreadPoolExecutor executor, final int threadPerCoreThreads, final int warmUpTimeSec)
	{
		final AtomicLong warmUpCounterThreads = new AtomicLong(0);
		final AtomicLong warmUpCounterSpins = new AtomicLong(0);

		final long warmUpTime = (long) Math.pow(10, 9) * warmUpTimeSec;
		final long warmUpThreads = executor.getPoolSize() * threadPerCoreThreads;

		LOG.debug("Warm Up Time: {}", warmUpTime);

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
}
