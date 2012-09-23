package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/21/12
 * Time: 10:16 AM
 */
public class BenchmarkFDCoarseRead80Write10Delete10 extends BaseBenchmark
{
	private FDCoarse<Double> list;

	private FDCoarse<Double>.Cursor cursors[];

	public BenchmarkFDCoarseRead80Write10Delete10(int threads, long initialListSize)
	{
		super(BenchmarkFDCoarseRead80Write10Delete10.class.getSimpleName(), threads, initialListSize);
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

		while (running && !Thread.currentThread().isInterrupted()) {
			double decision = BenchmarkDriver.getRandomDouble(10);
			if(decision < 8)
			{
				Double value = reader.curr().value();
				reader.next();
				readCount.incrementAndGet();
			} else if(decision < 8.5)
			{
				reader.writer().insertBefore(BenchmarkDriver.getRandomDouble());
				reader.next();
				writeCount.incrementAndGet();
			} else if(decision < 9.0)
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
