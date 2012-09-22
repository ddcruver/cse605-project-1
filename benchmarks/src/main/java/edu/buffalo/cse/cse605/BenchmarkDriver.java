package edu.buffalo.cse.cse605;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkDriver
{
	public void runIterations(final Benchmark benchmark, int threadPoolSize, int iterations) throws InterruptedException
	{
		for(int i = 0; i < iterations; i++)
		{
			runIteration(benchmark, threadPoolSize);
		}
	}

	public void runIteration(final Benchmark benchmark, final int threadPoolSize) throws InterruptedException
	{
		// Setup Thread Pool
		ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);
		ThreadPoolExecutor executor = (ThreadPoolExecutor) threadPool;
		executor.prestartAllCoreThreads();
		executor.setCorePoolSize(threadPoolSize);

		long currentActiveCount = warmUpThreadPool(executor, 2, 2);

		benchmark.initRun();

		final Semaphore running = new Semaphore(0);
	    for(int i = 0; i < threadPoolSize; i++)
	    {
			executor.submit(new Runnable()
			{
				@Override
				public void run()
				{

				   benchmark.run();
				   running.release();
				}
			});
	    }

		System.out.println("Before Set Running");
		benchmark.setRunning(false);
		System.out.println("Set Running False");
		running.acquire(threadPoolSize);

		System.out.println("After Running");



		//long afterCompletedCount = executor.getCompletedTaskCount();
		//while(afterCompletedCount < currentActiveCount + threadPoolSize);
		//{
		//	System.out.println("Waiting for test to end");
		//	afterCompletedCount = executor.getCompletedTaskCount();
		//}

		System.out.println("Ending Test");



		shutdownThreadPoolExecutor(executor);
	}

	private static long warmUpThreadPool(ThreadPoolExecutor executor, final int threadPerCoreThreads, final int warmUpTimeSec)
	{
		final AtomicInteger warmUpCounterThreads = new AtomicInteger(0);
		final AtomicInteger warmUpCounterSpins = new AtomicInteger(0);

		final long warmUpSpins = 1000 * 1000 * 10;
		final long warmUpTime = (long)Math.pow(10, 9) * warmUpTimeSec;

		final long warmUpThreads = executor.getPoolSize() * threadPerCoreThreads;

		System.out.println("Warm Up Time: " + warmUpTime);

		for(int i = 0; i < warmUpThreads; i++)
		{
			executor.submit(new Runnable() {
				@Override
				public void run() {

					long startTime = System.nanoTime();
					long endTime = startTime + warmUpTime;

					warmUpCounterThreads.incrementAndGet();

					long currentTime = 0;
					while(currentTime <= endTime)
					{
						currentTime = System.nanoTime();
						warmUpCounterSpins.incrementAndGet();
					}
				}
			});
		}

		long completedTasks = -1;
		while(completedTasks < warmUpThreads)
		{
			completedTasks = executor.getCompletedTaskCount();
		}

		System.out.println("Warm Up Counter Threads: " + warmUpCounterThreads);
		System.out.println("Warm Up Counter Spins: " + warmUpCounterSpins);
		System.out.println("Warmed Up");

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
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		executor.shutdownNow();
	}


	public static Double getRandomDouble() {
		return Math.random();
	}
}
