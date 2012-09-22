package edu.buffalo.cse.cse605;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/21/12
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkFDCoarse
{
	// Threads: threads to use
	// Inserts: how many inserts to do
	// Deletes: how many deletes to do
	// Reads: how many reads to do

	public static void main(String args[]) throws InterruptedException
	{
		if(args.length < 2)
		{
			System.out.println("Did not provide the valid amount of arguments");
			System.exit(-1);
		}
		int threads = Integer.parseInt(args[0]);

		int iterations = Integer.parseInt(args[1]);

		Benchmark benchmark = new BenchmarkFDCoarseReadOnlyTest();
		BenchmarkDriver driver = new BenchmarkDriver();
		driver.runIterations(benchmark, threads, iterations);
	}

	private static void warmUpThreadPool(ThreadPoolExecutor executor, final int threadPerCoreThreads, final int warmUpTimeSec)
	{
		final AtomicInteger warmUpCounterThreads = new AtomicInteger(0);
		final AtomicInteger warmUpCounterSpins = new AtomicInteger(0);

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
	}


}
