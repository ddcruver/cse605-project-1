package edu.buffalo.cse.cse605;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/21/12
 * Time: 10:17 AM
 */
public class BenchmarkFDListRWReadyOnly extends BaseBenchmark
{
	private FDListRW<Double> list;

	private FDListRW<Double>.Cursor cursors[];

	public BenchmarkFDListRWReadyOnly(int threads, long initialListSize)
	{
		super(BenchmarkFDListRWReadyOnly.class.getSimpleName(), threads, initialListSize);
		cursors = new FDListRW.Cursor[threads];
	}

	@Override
	public void initThread(int threadNumber) throws InterruptedException
	{
		FDListRW<Double>.Cursor reader = list.reader(list.head());

		long skips = BenchmarkDriver.getRandomDouble(initialListSize).longValue();
		for(int s = 0; s < skips; s++)
		{
			reader.next();
		}

		cursors[threadNumber] = reader;
	}

	@Override
	public void initRun() throws InterruptedException
	{
		running = Boolean.TRUE;
		readCount = new AtomicLong(0);
		writeCount = new AtomicLong(0);
		deleteCount = new AtomicLong(0);

		list = new FDListRW<Double>(0.0);

		FDListRW<Double>.Cursor reader = list.reader(list.head());

		for(int i = 0; i < initialListSize; i++)
		{
			reader.writer().insertAfter(BenchmarkDriver.getRandomDouble());
			reader.next();
		}
	}

	@Override
	public void run(int threadNumber) throws InterruptedException
	{
		// Get this threads cursor
		FDListRW<Double>.Cursor reader = cursors[threadNumber];
		boolean add = true;

		long reads = 0;

		while (running && !Thread.currentThread().isInterrupted()) {
			Double value = reader.curr().value();
			reader.next();
			readCount.incrementAndGet();
		}

		readCount.addAndGet(reads);
	}
}
