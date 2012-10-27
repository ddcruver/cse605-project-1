package edu.buffalo.cse.cse605.benchmarks;

import edu.buffalo.cse.cse605.benchmark.BaseBenchmark;
import edu.buffalo.cse.cse605.benchmark.BenchmarkDriver;
import edu.buffalo.cse.cse605.FDListFine;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/21/12
 * Time: 10:16 AM
 */
public class BenchmarkFDFineListRead50Write25Delete25 extends BaseBenchmark
{
    private FDListFine<Double> list;

    private FDListFine<Double>.Cursor cursors[];

    public BenchmarkFDFineListRead50Write25Delete25(int threads, long initialListSize)
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
        errorCount = new AtomicLong(0);

        list = new FDListFine<Double>(0.0);

        FDListFine<Double>.Cursor reader = list.reader(list.head());

        for (int i = 0; i < initialListSize; i++)
        {
            reader.writer().insertAfter(BenchmarkDriver.getRandomDouble());
            reader.next();
        }
    }

    @Override
    public void initThread(int threadNumber)
    {
        FDListFine<Double>.Cursor reader = list.reader(list.head());

        long skips = BenchmarkDriver.getRandomDouble(initialListSize).longValue();
        for (int s = 0; s < skips; s++)
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
                if (decision < 5.0)
                {
                    Double value = reader.curr().value();
                    reader.next();
                    reads++;
                } else if (decision < 6.5)
                {
                    reader.writer().insertBefore(BenchmarkDriver.getRandomDouble());
                    reader.next();
                    writes++;
                } else if (decision < 7.5)
                {
                    reader.writer().insertAfter(BenchmarkDriver.getRandomDouble());
                    reader.next();
                    writes++;
                } else
                {
                    reader.writer().delete();
                    reader.next();
                    deletes++;
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
        return "FDListFine 50% Reads 25% Writes 25% Deletes";
    }

}
