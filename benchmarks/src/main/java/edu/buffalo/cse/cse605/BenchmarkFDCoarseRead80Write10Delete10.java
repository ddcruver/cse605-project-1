package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/21/12
 * Time: 10:16 AM
 */
public class BenchmarkFDCoarseRead80Write10Delete10
{
	private static final transient Logger LOG = LoggerFactory.getLogger(BenchmarkFDCoarseRead80Write10Delete10.class);

	public static void main(String args[]) throws InterruptedException
	{
		if(args.length < 3)
		{
			LOG.error("Did not provide the valid amount of arguments: <threads> <iterations> <initialListSize>");
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
