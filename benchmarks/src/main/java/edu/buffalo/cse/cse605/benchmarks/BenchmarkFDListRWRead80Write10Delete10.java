package edu.buffalo.cse.cse605.benchmarks;

import edu.buffalo.cse.cse605.benchmark.BaseBenchmark;
import edu.buffalo.cse.cse605.benchmark.BenchmarkDriver;
import edu.buffalo.cse.cse605.FDListRW;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/21/12
 * Time: 10:16 AM
 */
public class BenchmarkFDListRWRead80Write10Delete10 extends BaseBenchmark
{
    private FDListRW<Double> list;

    private FDListRW<Double>.Cursor cursors[];

    public BenchmarkFDListRWRead80Write10Delete10(int threads, long initialListSize)
    {
        super(threads, initialListSize);
        cursors = new FDListRW.Cursor[threads];
    }

    @Override
    public void initRun() throws InterruptedException
    {
        running = Boolean.TRUE;
        readCount = new AtomicLong(0);
        writeCount = new AtomicLong(0);
        deleteCount = new AtomicLong(0);
        errorCount = new AtomicLong(0);

        list = new FDListRW<Double>(0.0);

        FDListRW<Double>.Cursor reader = list.reader(list.head());

        for (int i = 0; i < initialListSize; i++)
        {
            reader.writer().insertAfter(BenchmarkDriver.getRandomDouble());
            reader.next();
        }
    }

    @Override
    public void initThread(int threadNumber) throws InterruptedException
    {
        FDListRW<Double>.Cursor reader = list.reader(list.head());

        long skips = BenchmarkDriver.getRandomDouble(initialListSize).longValue();
        for (int s = 0; s < skips; s++)
        {
            reader.next();
        }

        cursors[threadNumber] = reader;
    }

    @Override
    public void run(int threadNumber) throws InterruptedException
    {
        // Get this threads cursor
        FDListRW<Double>.Cursor reader = cursors[threadNumber];
        boolean add = true;

        long reads = 0;
        long writes = 0;
        long deletes = 0;
        long errors = 0;

        while (running && !Thread.currentThread().isInterrupted())
        {
            try
            {
	            if(reader.curr().isDeleted())
		            reader = list.reader(list.head());

	            double decision = BenchmarkDriver.getRandomDouble(10);
                if (decision < 8)
                {
                    Double value = reader.curr().value();
                    reader.next();
                    readCount.incrementAndGet();
                } else if (decision < 8.5)
                {
                    reader.writer().insertBefore(BenchmarkDriver.getRandomDouble());
                    reader.next();
                    writeCount.incrementAndGet();
                } else if (decision < 9.0)
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
            } catch (Exception e)
            {
                //LOG.error("Test encountered error", e);
                errors++;
            }
        }

        readCount.addAndGet(reads);
        writeCount.addAndGet(writes);
        deleteCount.addAndGet(deletes);
        errorCount.addAndGet(errors);
    }

    @Override
    public String getTestName()
    {
        return "FDListRW 80% Reads 10% Writes 10% Deletes";
    }

}
