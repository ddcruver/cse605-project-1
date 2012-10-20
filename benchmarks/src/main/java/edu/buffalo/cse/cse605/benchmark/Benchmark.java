package edu.buffalo.cse.cse605.benchmark;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 12:32 PM
 */
public interface Benchmark
{
	public void initRun() throws InterruptedException;
	public void initThread(int threadNumber) throws InterruptedException;
	public void run(int threadNumber) throws InterruptedException;

	public boolean getRunning();
	public void setRunning(boolean value);

	public AtomicLong getReadAtomicCount();
	public AtomicLong getWriteAtomicCount();
	public AtomicLong getDeleteAtomicCount();
    public AtomicLong getErrorAtomicCount();

	public String getTestName();
}
