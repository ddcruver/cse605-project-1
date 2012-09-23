package edu.buffalo.cse.cse605;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/21/12
 * Time: 10:16 AM
 */
public class BenchmarkFDListRead50Write25Delete25 extends BaseBenchmark
{
	private FDList<Double> list;

	private FDList<Double>.Cursor cursors[];

	public BenchmarkFDListRead50Write25Delete25(int threads, long initialListSize)
	{
		super(BenchmarkFDListRead50Write25Delete25.class.getSimpleName(), threads, initialListSize);
		cursors = new FDList.Cursor[threads];
	}

	@Override
	public void initRun() throws InterruptedException
	{
		running = Boolean.TRUE;
		readCount = new AtomicLong(0);
		writeCount = new AtomicLong(0);
		deleteCount = new AtomicLong(0);

		list = new FDList<Double>(0.0);

		FDList<Double>.Cursor reader = list.reader(list.head());

		for(int i = 0; i < initialListSize; i++)
		{
			reader.writer().insertAfter(BenchmarkDriver.getRandomDouble());
			reader.next();
		}
	}

	@Override
	public void initThread(int threadNumber) throws InterruptedException
	{
		FDList<Double>.Cursor reader = list.reader(list.head());

		long skips = BenchmarkDriver.getRandomDouble(initialListSize).longValue();
		for(int s = 0; s < skips; s++)
		{
			reader.next();
		}

		cursors[threadNumber] = reader;
	}

	@Override
	public void run(int threadNumber) throws InterruptedException
	{
		// Get this threads cursor
		FDList<Double>.Cursor reader = cursors[threadNumber];
		boolean add = true;

		while (running && !Thread.currentThread().isInterrupted()) {
			double decision = BenchmarkDriver.getRandomDouble(10);
			if(decision < 5.0)
			{
				Double value = reader.curr().value();
				reader.next();
				readCount.incrementAndGet();
			} else if(decision < 6.5)
			{
				reader.writer().insertBefore(BenchmarkDriver.getRandomDouble());
				reader.next();
				writeCount.incrementAndGet();
			} else if(decision < 7.5)
			{
				reader.writer().insertAfter(BenchmarkDriver.getRandomDouble());
				reader.next();
				writeCount.incrementAndGet();
			} else
			{
				reader.writer().delete();
				reader.next();
				deleteCount.incrementAndGet();
			}
		}
	}

}