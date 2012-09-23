package edu.buffalo.cse.cse605;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Benchmark
{
	public void initRun();
	public void initThread(int threadNumber);
	public void run(int threadNumber);

	public boolean getRunning();
	public void setRunning(boolean value);

	public AtomicLong getReadAtomicCount();
	public AtomicLong getWriteAtomicCount();
	public AtomicLong getDeleteAtomicCount();
}