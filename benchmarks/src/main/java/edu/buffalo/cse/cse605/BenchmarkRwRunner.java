package edu.buffalo.cse.cse605;

import edu.buffalo.cse.cse605.benchmark.Benchmark;
import edu.buffalo.cse.cse605.benchmark.BenchmarkDriver;
import edu.buffalo.cse.cse605.benchmarks.BenchmarkFDListRWRead50Write25Delete25;
import edu.buffalo.cse.cse605.benchmarks.BenchmarkFDListRWRead80Write10Delete10;
import edu.buffalo.cse.cse605.benchmarks.BenchmarkFDListRWReadyOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 9:16 PM
 */
public class BenchmarkRwRunner
{
	private static final transient Logger LOG = LoggerFactory.getLogger(BenchmarkRwRunner.class);

	public static void main(String args[]) throws InterruptedException, IOException
	{
		if (args.length < 4)
		{
			LOG.error("You did not provide the valid amount of arguments: <threads> <iterations> <secondsToRunEachIteration> <initialListSize>");
			System.exit(-1);
		}
		int threads = Integer.parseInt(args[0]);
		int iterations = Integer.parseInt(args[1]);
		int secondsToRun = Integer.parseInt(args[2]);
		long initialListSize = Long.parseLong(args[3]);

		BenchmarkDriver driver = new BenchmarkDriver();
        driver.start("benchmarks-rw", threads, iterations, secondsToRun, initialListSize);

        Benchmark rwReadOnly = new BenchmarkFDListRWReadyOnly(threads, initialListSize);
		driver.runIterations(rwReadOnly, secondsToRun, threads, iterations);

		Benchmark rw801010 = new BenchmarkFDListRWRead80Write10Delete10(threads, initialListSize);
		driver.runIterations(rw801010, secondsToRun, threads, iterations);

		Benchmark rw502525 = new BenchmarkFDListRWRead50Write25Delete25(threads, initialListSize);
		driver.runIterations(rw502525, secondsToRun, threads, iterations);

        driver.stop();
    }
}
