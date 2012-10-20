package edu.buffalo.cse.cse605;

import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: delvecchio
 * Date: 9/8/12
 * Time: 4:50 PM
 */
public class FDListRWTest {
	private Logger log = LoggerFactory.getLogger(FDListRWTest.class);

	private final int numberOfThreads = 10;

	@Test
	public void threadAccessToListInsertAndRemoveTest() throws InterruptedException {
//        for(int i = 0; i < 100; i++){
		addAndRemoveElements(numberOfThreads, 30000, 4, 1);
		addAndRemoveElements(numberOfThreads, 30000, 4, 2);
//        }
		//addAndRemoveElements(numberOfThreads, 1000, 1, 1);
		//addAndRemoveElements(numberOfThreads, 1000, 3, 4);
	}

	private void addAndRemoveElements(int threadPoolSize, int totalListOperations, int numberInserts, int numberDeletes) throws InterruptedException {
		final FDListRW<Double> list = createListOfSizeOne();
		final AtomicInteger inserts = new AtomicInteger(1);
		final AtomicInteger deletes = new AtomicInteger(0);
		final AtomicBoolean continueRun = new AtomicBoolean(true);
		Queue<Double> comparisonList = new ConcurrentLinkedQueue<Double>();
		comparisonList.add(0.0);

		addAndRemoveFromList(list, inserts, deletes, continueRun, threadPoolSize, numberInserts, numberDeletes, comparisonList, totalListOperations);
		printSummaryResults(inserts, deletes);
		log.info(comparisonList.size() + " vs " + (inserts.get() - deletes.get()));
		testResults(list, comparisonList.size()/*inserts.get() - deletes.get()*/);
		log.info("Done checking results.");
	}

	private void testResults(FDListRW<Double> list, int listSize) throws InterruptedException
    {
		FDListRW<Double>.Element head = list.head();
		FDListRW<Double>.Cursor reader = list.reader(list.head());

		reader.next();
		boolean isHead = false;
		int count = 0;
		while (!isHead) {
			if (reader.curr() == list.head()) {
				isHead = true;
			} else if (reader.curr().isDeleted()) {
				Assert.fail("We have a deleted node in the list ... ");
			} else {
				count++;
				reader.next();
			}
		}

		Assert.assertEquals("Expected list size different from size: ", listSize, count);

	}

	private void printSummaryResults(AtomicInteger inserts, AtomicInteger deletes) {
		log.info("Done");
		log.info("Added " + inserts.get());
		log.info("Removed " + deletes.get());
	}

	private void addAndRemoveFromList(final FDListRW<Double> list, final AtomicInteger insertSize, final AtomicInteger removeSize,
									  final AtomicBoolean continueRun, int threadPoolSize,
									  final int numberInserts, final int numberDeletes, final Queue<Double> comparisonList, final int totalListOperations) {
		final AtomicBoolean continueExecution = new AtomicBoolean(true);
		// Create the ThreadPool
		final ExecutorService tpe = Executors.newFixedThreadPool(threadPoolSize);
		for (int i = 0; i < threadPoolSize; i++) {
			tpe.submit(new Runnable() {
				@Override
				public void run() {
					FDListRW<Double>.Cursor reader = null;
					try {
						reader = list.reader(list.head());
					} catch (InterruptedException e) {
						log.info("Interrupted..");
					}
					boolean add = true;
					while (continueRun.get() && !Thread.currentThread().isInterrupted() && continueExecution.get()) {
						try {
							if (add) {
								for (int i = 0; i < numberInserts; i++) {
									reader.writer().insertAfter(getRandomDouble());
									insertSize.incrementAndGet();
									comparisonList.add(0.0);

								}
								add = false;

							} else {
								reader.next();
								for (int i = 0; i < numberDeletes; i++) {
									reader.writer().delete();
									removeSize.incrementAndGet();
									comparisonList.remove();
								}
								add = true;
							}
						} catch (IllegalStateException e) {
							log.trace("Found a deleted node ... we have to reinitialize the reader.");
							try {
								reader = list.reader(list.head());
							} catch (InterruptedException e1) {
								log.warn("Interrupted.");
							}
						} catch (InterruptedException e) {
							log.debug("Interrupted.");
						}
					}
				}
			});

		}

		log.info("Sleeping...");
		try {
			Thread.sleep(totalListOperations);
		} catch (InterruptedException e) {
		}
		continueExecution.set(false);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		//////////////////////////////////////////////

		log.info("Shutting down now...");
		tpe.shutdownNow();
		try {
			tpe.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}

		log.info("Shut down pool.");

	}

	private FDListRW<Double> createListOfSizeOne() throws InterruptedException {
		// Create the list
		final FDListRW<Double> list = new FDListRW<Double>(getRandomDouble());
		FDListRW<Double>.Cursor reader = list.reader(list.head());
		reader.writer().insertAfter(getRandomDouble());
		return list;
	}

	public static Double getRandomDouble() {
		return Math.random();
	}
}
