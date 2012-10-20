package edu.buffalo.cse.cse605.benchmarks;

import edu.buffalo.cse.cse605.benchmark.BaseBenchmark;
import edu.buffalo.cse.cse605.benchmark.BenchmarkDriver;
import edu.buffalo.cse.cse605.FDList;

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
        super(threads, initialListSize);
        cursors = new FDList.Cursor[threads];
    }

    @Override
    public void initRun() throws InterruptedException
    {
        running = Boolean.TRUE;
        readCount = new AtomicLong(0);
        writeCount = new AtomicLong(0);
        deleteCount = new AtomicLong(0);
        errorCount = new AtomicLong(0);

        list = new FDList<Double>(0.0);

        FDList<Double>.Cursor reader = list.reader(list.head());

        for (int i = 0; i < initialListSize; i++)
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
        FDList<Double>.Cursor reader = cursors[threadNumber];

        long reads = 0;
        long writes = 0;
        long deletes = 0;
        long errors = 0;

        while (running && !Thread.currentThread().isInterrupted())
        {
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
        }

        readCount.addAndGet(reads);
        writeCount.addAndGet(writes);
        deleteCount.addAndGet(deletes);
    }

    @Override
    public String getTestName()
    {
        return "FDList 50% Reads 25% Writes 25% Deletes";
    }

}
