package edu.buffalo.cse.cse605;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/22/12
 * Time: 8:44 PM
 */
public class BenchmarkResult
{
	final long reads;
	final long writes;
	final long deletes;

	public BenchmarkResult(Benchmark benchmark)
	{
		reads = benchmark.getReadAtomicCount().get();
		writes = benchmark.getWriteAtomicCount().get();
		deletes = benchmark.getDeleteAtomicCount().get();
	}

	public BenchmarkResult(long r, long w, long d)
	{
		reads = r;
		writes = w;
		deletes = d;
	}

	public long getReads()
	{
		return reads;
	}

	public long getWrites()
	{
		return writes;
	}

	public long getDeletes()
	{
		return deletes;
	}

	public String toCsv()
	{
		return reads + "," + writes + "," + deletes;
	}
}
