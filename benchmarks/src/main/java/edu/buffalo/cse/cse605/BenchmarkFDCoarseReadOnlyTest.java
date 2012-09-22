package edu.buffalo.cse.cse605;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkFDCoarseReadOnlyTest implements Benchmark
{
	private AtomicInteger readCount;
	private AtomicInteger writeCount;
	private AtomicInteger deleteCount;
	private volatile boolean running;
	private FDCoarse<Double> list;

	@Override
	public void initRun()
	{
		running = Boolean.TRUE;
		readCount = new AtomicInteger(0);
		writeCount = new AtomicInteger(0);
		deleteCount = new AtomicInteger(0);

		list = new FDCoarse<Double>(0.0);

		FDCoarse<Double>.Cursor reader = list.reader(list.head());

		long readListSize = 10000;

		for(int i = 0; i < readListSize; i++)
		{
				reader.writer().insertAfter(BenchmarkDriver.getRandomDouble());
				reader.next();
		}

	}

	@Override
	public void run()
	{
		FDCoarse<Double>.Cursor reader = list.reader(list.head());
		boolean add = true;

		while (running && !Thread.currentThread().isInterrupted()) {
			Double value = reader.curr().value();
			reader.next();
			readCount.incrementAndGet();
		}

	}

	@Override
	public boolean getRunning()
	{
		return running;
	}

	@Override
	public void setRunning(boolean value)
	{
		running = value;
	}

	@Override
	public AtomicInteger getReadAtomicInteger()
	{
		return readCount;
	}

	@Override
	public AtomicInteger getWriteAtomicInteger()
	{
		return writeCount;
	}

	@Override
	public AtomicInteger getDeleteAtomicInteger()
	{
		return deleteCount;
	}
}
