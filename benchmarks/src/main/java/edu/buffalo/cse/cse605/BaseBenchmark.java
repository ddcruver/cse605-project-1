package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 8:54 PM
 */
public abstract class BaseBenchmark implements Benchmark
{
	private static final transient Logger LOG = LoggerFactory.getLogger(BaseBenchmark.class);
	protected AtomicLong readCount;
	protected AtomicLong writeCount;
	protected AtomicLong deleteCount;

	protected final int threads;
	protected final long initialListSize;

	protected volatile boolean running;

	public BaseBenchmark(int threadCount, long initialListSize)
	{
		threads = threadCount;
		this.initialListSize =  initialListSize;
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
	public AtomicLong getReadAtomicCount()
	{
		return readCount;
	}

	@Override
	public AtomicLong getWriteAtomicCount()
	{
		return writeCount;
	}

	@Override
	public AtomicLong getDeleteAtomicCount()
	{
		return deleteCount;
	}


}
