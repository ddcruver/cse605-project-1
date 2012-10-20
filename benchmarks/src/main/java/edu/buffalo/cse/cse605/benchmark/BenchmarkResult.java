package edu.buffalo.cse.cse605.benchmark;

import edu.buffalo.cse.cse605.benchmark.Benchmark;

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
    final long errors;

	public BenchmarkResult(Benchmark benchmark)
	{
		reads = benchmark.getReadAtomicCount().get();
		writes = benchmark.getWriteAtomicCount().get();
		deletes = benchmark.getDeleteAtomicCount().get();
        errors = benchmark.getErrorAtomicCount().get();
	}

	public BenchmarkResult(long r, long w, long d, long e)
	{
		reads = r;
		writes = w;
		deletes = d;
        errors = e;
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

    public long getErrors()
    {
        return errors;
    }


	public String toCsv()
	{
		return (reads + writes + deletes) + "," + reads + "," + writes + "," + deletes + "," + errors;
	}
}
