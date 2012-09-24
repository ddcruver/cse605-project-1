package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 9:16 PM
 */
public class BenchmarkCoarseAndRwRunner
{
	private static final transient Logger LOG = LoggerFactory.getLogger(BenchmarkCoarseAndRwRunner.class);

	public static void main(String args[]) throws InterruptedException, IOException
	{
		//startSleeperThread();

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

		BufferedWriter rawOut = getRawResultWriter(threads, iterations, secondsToRun, initialListSize);
		BufferedWriter normalizedOut = getResultWriter(threads, iterations, secondsToRun, initialListSize);

		Benchmark coarseReadOnly = new BenchmarkFDCoarseReadOnly(threads, initialListSize);
		List<BenchmarkResult> coarseReadOnlyResults = driver.runIterations(coarseReadOnly, secondsToRun, threads, iterations);
		writeResults("FDCoarse 100% Reads", coarseReadOnlyResults, rawOut, normalizedOut);

		Benchmark rwReadOnly = new BenchmarkFDListRWReadyOnly(threads, initialListSize);
		List<BenchmarkResult> rwReadOnlyResults = driver.runIterations(rwReadOnly, secondsToRun, threads, iterations);
		writeResults("FDListRW 100% Reads", rwReadOnlyResults, rawOut, normalizedOut);

		Benchmark coarse801010 = new BenchmarkFDCoarseRead80Write10Delete10(threads, initialListSize);
		List<BenchmarkResult> coarse801010Results = driver.runIterations(coarse801010, secondsToRun, threads, iterations);
		writeResults("FDCoarse 80% Reads 10% Writes 10% Deletes", coarse801010Results, rawOut, normalizedOut);

		Benchmark rw801010 = new BenchmarkFDListRWRead80Write10Delete10(threads, initialListSize);
		List<BenchmarkResult> rw801010Results = driver.runIterations(rw801010, secondsToRun, threads, iterations);
		writeResults("FDListRW 80% Reads 10% Writes 10% Deletes", rw801010Results, rawOut, normalizedOut);

		Benchmark coarse502525 = new BenchmarkFDCoarseRead50Write25Delete25(threads, initialListSize);
		List<BenchmarkResult> coarse502525Results = driver.runIterations(coarse502525, secondsToRun, threads, iterations);
		writeResults("FDCoarse 50% Reads 25% Writes 25% Deletes", coarse502525Results, rawOut, normalizedOut);

		Benchmark rw502525 = new BenchmarkFDListRWRead50Write25Delete25(threads, initialListSize);
		List<BenchmarkResult> rw5025250Results = driver.runIterations(rw502525, secondsToRun, threads, iterations);
		writeResults("FDListRW 50% Reads 25% Writes 25% Deletes", rw5025250Results, rawOut, normalizedOut);

		rawOut.close();
		normalizedOut.close();

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

	private static String getBaseFileName(int threads, int iterations, int secondsToRun, long initialListSize)
	{
		return "coarse-and-rw-benchmark-" + threads + "t-" + iterations + "i-" + secondsToRun + "s-" + initialListSize + "ils";
	}

	private static BufferedWriter getRawResultWriter(int threads, int iterations, int secondsToRun, long initialListSize) throws IOException
	{
		String filename = getBaseFileName(threads, iterations, secondsToRun, initialListSize) + "-raw.out";

		// Delete existing report file if it already exists
		File file = new File(filename);
		if (file.exists())
			file.delete();

		FileWriter rawFileWriter = new FileWriter(filename);
		return new BufferedWriter(rawFileWriter);
	}

	private static BufferedWriter getResultWriter(int threads, int iterations, int secondsToRun, long initialListSize) throws IOException
	{
		String filename = getBaseFileName(threads, iterations, secondsToRun, initialListSize) + ".out";
		FileWriter rawFileWriter = new FileWriter(filename);
		return new BufferedWriter(rawFileWriter);
	}

	private static void writeResults(String testTitle, List<BenchmarkResult> benchmarkResults, BufferedWriter rawOut, BufferedWriter normalizedOut) throws IOException
	{
		writeRawResults(testTitle, benchmarkResults, rawOut);
		writeNormalizedResults(testTitle, benchmarkResults, normalizedOut);
	}

	private static void writeRawResults(String testTitle, List<BenchmarkResult> benchmarkResults, BufferedWriter out) throws IOException
	{
		int iteration = 1;
		for (BenchmarkResult result : benchmarkResults)
		{
			out.write(testTitle);
			out.write(",");
			out.write(Integer.toString(iteration));
			iteration++;
			out.write(",");
			out.write(result.toCsv());
			out.write("\n");
			out.flush();
		}
	}

	private static void writeNormalizedResults(String testTitle, List<BenchmarkResult> benchmarkResults, BufferedWriter out) throws IOException
	{
		long iteration = 0;
		long averageCounts = 0;
		out.write(testTitle);
		out.write(",");

		long totalOperations;
		long totalReads = 0;
		long totalWrites = 0;
		long totalDeletes = 0;

		long skipFirst = 1;

		for (BenchmarkResult result : benchmarkResults)
		{
			if (iteration < skipFirst)
			{
				totalReads += result.getReads();
				totalWrites += result.getWrites();
				totalDeletes += result.getDeletes();
				averageCounts++;
			}
			iteration++;
		}

		totalOperations = totalReads + totalWrites + totalDeletes;
		out.write(Long.toString(totalOperations / averageCounts));
		out.write(",");
		out.write(Long.toString(totalReads / averageCounts));
		out.write(",");
		out.write(Long.toString(totalWrites / averageCounts));
		out.write(",");
		out.write(Long.toString(totalDeletes / averageCounts));
		out.write("\n");
		out.flush();
	}
}
