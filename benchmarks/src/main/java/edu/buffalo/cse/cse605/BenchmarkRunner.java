package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 9:16 PM
 */
public class BenchmarkRunner
{
	private static final transient Logger LOG = LoggerFactory.getLogger(BenchmarkRunner.class);

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
		List<BenchmarkResult> results = driver.runIterations(benchmark, threads, iterations);

		Benchmark benchmark2 = new BenchmarkFDFineListReadOnly(threads, initialListSize);
		BenchmarkDriver driver2 = new BenchmarkDriver();
		List<BenchmarkResult> results2 = driver.runIterations(benchmark2, threads, iterations);

		Benchmark benchmark3 = new BenchmarkFDListRWReadyOnly(threads, initialListSize);
		BenchmarkDriver driver3 = new BenchmarkDriver();
		List<BenchmarkResult> results3 = driver.runIterations(benchmark3, threads, iterations);
	}

}
