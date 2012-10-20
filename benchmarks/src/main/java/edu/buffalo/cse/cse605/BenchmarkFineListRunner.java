package edu.buffalo.cse.cse605;

import edu.buffalo.cse.cse605.benchmark.Benchmark;
import edu.buffalo.cse.cse605.benchmark.BenchmarkDriver;
import edu.buffalo.cse.cse605.benchmarks.BenchmarkFDFineListRead50Write25Delete25;
import edu.buffalo.cse.cse605.benchmarks.BenchmarkFDFineListRead80Write10Delete10;
import edu.buffalo.cse.cse605.benchmarks.BenchmarkFDFineListReadOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 9:16 PM
 */
public class BenchmarkFineListRunner
{
	private static final transient Logger LOG = LoggerFactory.getLogger(BenchmarkFineListRunner.class);

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
        driver.start("benchmarks-fine", threads, iterations, secondsToRun, initialListSize);

		Benchmark fineReadOnly = new BenchmarkFDFineListReadOnly(threads, initialListSize);
		driver.runIterations(fineReadOnly, secondsToRun, threads, iterations);

		Benchmark fine801010 = new BenchmarkFDFineListRead80Write10Delete10(threads, initialListSize);
		driver.runIterations(fine801010, secondsToRun, threads, iterations);

		Benchmark fine502525 = new BenchmarkFDFineListRead50Write25Delete25(threads, initialListSize);
		driver.runIterations(fine502525, secondsToRun, threads, iterations);

		driver.stop();
	}
}
