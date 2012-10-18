package edu.buffalo.cse.cse605;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 1:09 PM
 */
public class BenchmarkFDCoarseReadOnly extends BaseBenchmark
{
	private FDCoarse<Double> list;

	private FDCoarse<Double>.Cursor cursors[];

	public BenchmarkFDCoarseReadOnly(int threads, long initialListSize)
	{
		super(BenchmarkFDCoarseReadOnly.class.getSimpleName(), threads, initialListSize);
		cursors = new FDCoarse.Cursor[threads];
	}

	@Override
	public void initRun()
	{
		running = Boolean.TRUE;
		readCount = new AtomicLong(0);
		writeCount = new AtomicLong(0);
		deleteCount = new AtomicLong(0);

		list = new FDCoarse<Double>(0.0);

		FDCoarse<Double>.Cursor reader = list.reader(list.head());

		for(int i = 0; i < initialListSize; i++)
		{
				reader.writer().insertAfter(BenchmarkDriver.getRandomDouble());
				reader.next();
		}
	}

	@Override
	public void initThread(int threadNumber)
	{
		FDCoarse<Double>.Cursor reader = list.reader(list.head());

		long skips = BenchmarkDriver.getRandomDouble(initialListSize).longValue();
		for(int s = 0; s < skips; s++)
		{
			reader.next();
		}

		cursors[threadNumber] = reader;
	}

	@Override
	public void run(int threadNumber)
	{
		// Get this threads cursor
		FDCoarse<Double>.Cursor reader = cursors[threadNumber];
		boolean add = true;

		long reads = 0;

		while (running && !Thread.currentThread().isInterrupted()) {
			Double value = reader.curr().value();
			reader.next();
			reads++;
		}

		readCount.addAndGet(reads);
	}
}
