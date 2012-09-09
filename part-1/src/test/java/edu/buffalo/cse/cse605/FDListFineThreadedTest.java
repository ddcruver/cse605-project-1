package edu.buffalo.cse.cse605;

import junit.framework.Assert;
import org.junit.Test;

import java.awt.geom.Dimension2D;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: delvecchio
 * Date: 9/8/12
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class FDListFineThreadedTest {

    @Test
    public void threadAccessToListInsertAndRemoveTest() throws InterruptedException {
        addAndRemoveElements(100, 100, 4, 1);
        addAndRemoveElements(100, 1000, 4, 2);
        addAndRemoveElements(100, 1000, 2, 2);
        addAndRemoveElements(100, 10000, 3, 4);
    }

    private void addAndRemoveElements(int threadPoolSize, int sleepTime, int numberInserts, int numberDeletes) throws InterruptedException {
        final FDListFine<Double> list = createListOfSizeOne();
        final AtomicInteger inserts = new AtomicInteger(1);
        final AtomicInteger deletes = new AtomicInteger(1);
        final AtomicBoolean continueRun = new AtomicBoolean(true);

        addAndRemoveFromList(list, inserts, deletes, continueRun, threadPoolSize, numberInserts, numberDeletes);
        sleepAndWakeThread(continueRun, sleepTime);
        printSummaryResults(inserts, deletes);
        testResults(list, inserts.get() - deletes.get());
    }

    private void testResults(FDListFine<Double> list, int listSize) {
        FDListFine.Element head = list.head();
        FDListFine<Double>.Cursor reader = list.reader(list.head());
        reader.next();
        boolean isHead = false;
        int count = 0;
        while(isHead  == false){
            if(reader.curr().value().compareTo((Double)head.value()) == 0){
                isHead = true;
            }
            else if(reader.curr().isDeleted()){

                Assert.fail("We have a deleted node in the list ... ");
            }
            else{
                count++;
                reader.next();
                //System.out.println(count + " " + reader.curr().value().doubleValue());
            }
        }

        Assert.assertEquals("Expected list size different from size: ", listSize + 1, count);

    }

    private void sleepAndWakeThread(AtomicBoolean continueRun, int sleepTime) throws InterruptedException {
        System.out.println("Sleep");
        Thread.sleep(sleepTime);
        System.out.println("Wake up");
        continueRun.set(false);
    }

    private void printSummaryResults(AtomicInteger inserts, AtomicInteger deletes) {
        System.out.println("Done");
        System.out.println("Added " + inserts.get());
        System.out.println("Removed " + deletes.get());
    }

    private void addAndRemoveFromList(final FDListFine<Double> list, final AtomicInteger insertSize, final AtomicInteger removeSize,
                                      final AtomicBoolean continueRun, int threadPoolSize,
                                      final int numberInserts, final int numberDeletes) {
        // Create the ThreadPool
        ExecutorService tpe = Executors.newFixedThreadPool(threadPoolSize);
        tpe.submit(new Runnable() {
            @Override
            public void run() {
                FDListFine<Double>.Cursor reader = list.reader(list.head());
                boolean add = true;
                while(continueRun.get()){
                    if(add){
                        for(int i =0; i < numberInserts; i++){
                            reader.writer().insertAfter(getRandomDouble());
                            insertSize.incrementAndGet();
                        }
                        add = false;

                    }
                    else{
                        try{
                            reader.next();
                            for(int i =0; i < numberDeletes; i++){
                                reader.writer().delete();
                                removeSize.incrementAndGet();
                            }
                            add = true;
                        }
                        catch (Exception e){
                            //e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private FDListFine<Double> createListOfSizeOne() {
        // Create the list
        final FDListFine<Double> list = new FDListFine<Double>(getRandomDouble());
        FDListFine<Double>.Cursor reader = list.reader(list.head());
        reader.writer().insertAfter(getRandomDouble());
        return list;
    }

    public static Double getRandomDouble() {
        return new Double(Math.random());
    }
}
