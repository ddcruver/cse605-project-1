package edu.buffalo.cse.cse605;

import edu.buffalo.cse.cse605.benchmark.Benchmark;
import edu.buffalo.cse.cse605.benchmark.BenchmarkDriver;
import edu.buffalo.cse.cse605.benchmark.BenchmarkResult;
import edu.buffalo.cse.cse605.benchmarks.*;
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
public class SingleThreadBenchmarkRunner
{
	private static final transient Logger LOG = LoggerFactory.getLogger(SingleThreadBenchmarkRunner.class);

	public static void main(String args[]) throws InterruptedException, IOException
	{
        if(args.length < 3)
        {
            LOG.error("You did not provide the valid amount of arguments: <iterations> <secondsToRunEachIteration> <initialListSize>");
            System.exit(-1);
        }
        int threads = 1;
        int iterations = Integer.parseInt(args[0]);
        int secondsToRun = Integer.parseInt(args[1]);
        long initialListSize = Long.parseLong(args[2]);

        BenchmarkDriver driver = new BenchmarkDriver();
        driver.start("benchmarks-single-threaded", threads, iterations, secondsToRun, initialListSize);

        Benchmark listReadOnly = new BenchmarkFDListReadOnly(threads, initialListSize);
        driver.runIterations(listReadOnly, secondsToRun, threads, iterations);

        Benchmark coarseReadOnly = new BenchmarkFDCoarseReadOnly(threads, initialListSize);
        driver.runIterations(coarseReadOnly, secondsToRun, threads, iterations);

        Benchmark fineReadOnly = new BenchmarkFDFineListReadOnly(threads, initialListSize);
        driver.runIterations(fineReadOnly, secondsToRun, threads, iterations);

        Benchmark rwReadOnly = new BenchmarkFDListRWReadyOnly(threads, initialListSize);
        driver.runIterations(rwReadOnly, secondsToRun, threads, iterations);

        Benchmark list801010 = new BenchmarkFDListRead80Write10Delete10(threads, initialListSize);
        driver.runIterations(list801010, secondsToRun, threads, iterations);

        Benchmark coarse801010 = new BenchmarkFDCoarseRead80Write10Delete10(threads, initialListSize);
        driver.runIterations(coarse801010, secondsToRun, threads, iterations);

        Benchmark fine801010 = new BenchmarkFDFineListRead80Write10Delete10(threads, initialListSize);
        driver.runIterations(fine801010, secondsToRun, threads, iterations);

        Benchmark rw801010 = new BenchmarkFDListRWRead80Write10Delete10(threads, initialListSize);
        driver.runIterations(fine801010, secondsToRun, threads, iterations);

        Benchmark list502525 = new BenchmarkFDListRead50Write25Delete25(threads, initialListSize);
        driver.runIterations(list502525, secondsToRun, threads, iterations);

        Benchmark coarse502525 = new BenchmarkFDCoarseRead50Write25Delete25(threads, initialListSize);
        driver.runIterations(coarse502525, secondsToRun, threads, iterations);

        Benchmark fine502525 = new BenchmarkFDFineListRead50Write25Delete25(threads, initialListSize);
        driver.runIterations(fine502525, secondsToRun, threads, iterations);

        Benchmark rw502525 = new BenchmarkFDListRWRead50Write25Delete25(threads, initialListSize);
        driver.runIterations(rw502525, secondsToRun, threads, iterations);

        driver.stop();
    }
}
