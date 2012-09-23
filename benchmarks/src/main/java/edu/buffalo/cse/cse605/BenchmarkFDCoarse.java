package edu.buffalo.cse.cse605;

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
		if(args.length < 3)
		{
			System.out.println("Did not provide the valid amount of arguments: <threads> <iterations> <initialListSize>");
			System.exit(-1);
		}
		int threads = Integer.parseInt(args[0]);
		int iterations = Integer.parseInt(args[1]);
		long initialListSize = Long.parseLong(args[2]);

		Benchmark benchmark = new BenchmarkFDCoarseReadOnly(threads, initialListSize);
		BenchmarkDriver driver = new BenchmarkDriver();
		driver.runIterations(benchmark, threads, iterations);
	}




}
