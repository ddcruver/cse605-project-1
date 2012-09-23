package edu.buffalo.cse.cse605;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/21/12
 * Time: 10:16 AM
 */
public class BenchmarkFDFineListReadOnly extends BaseBenchmark
{
	private FDListFine<Double> list;

	private FDListFine<Double>.Cursor cursors[];

	public BenchmarkFDFineListReadOnly(int threads, long initialListSize)
	{
		super(threads, initialListSize);
		cursors = new FDListFine.Cursor[threads];
	}

	@Override
	public void initRun()
	{
		running = Boolean.TRUE;
		readCount = new AtomicLong(0);
		writeCount = new AtomicLong(0);
		deleteCount = new AtomicLong(0);

		list = new FDListFine<Double>(0.0);

		FDListFine<Double>.Cursor reader = list.reader(list.head());

		for(int i = 0; i < initialListSize; i++)
		{
			reader.writer().insertAfter(BenchmarkDriver.getRandomDouble());
			reader.next();
		}
	}

	@Override
	public void initThread(int threadNumber)
	{
		FDListFine<Double>.Cursor reader = list.reader(list.head());

		long skips = (long) (Math.random() * (double)initialListSize);
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
		FDListFine<Double>.Cursor reader = cursors[threadNumber];
		boolean add = true;

		while (running && !Thread.currentThread().isInterrupted()) {
			Double value = reader.curr().value();
			reader.next();
			readCount.incrementAndGet();
		}
	}
}
