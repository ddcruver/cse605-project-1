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
public class FDListFineThreadedTest {

	private Logger log = LoggerFactory.getLogger(FDListFineThreadedTest.class);
	private final int numberOfThreads = 10;

	@Test
	public void threadAccessToListInsertAndRemoveTest() throws InterruptedException {
		addAndRemoveElements(numberOfThreads, 30000, 4, 1);
		addAndRemoveElements(numberOfThreads, 30000, 4, 2);
		addAndRemoveElements(numberOfThreads, 30000, 1, 1);
		addAndRemoveElements(numberOfThreads, 30000, 3, 4);
	}

	private void addAndRemoveElements(int threadPoolSize, int totalListOperations, int numberInserts, int numberDeletes) throws InterruptedException {
		final FDListFine<Double> list = createListOfSizeOne();
		final AtomicInteger inserts = new AtomicInteger(1);
		final AtomicInteger deletes = new AtomicInteger(0);
		final AtomicBoolean continueRun = new AtomicBoolean(true);
		Queue<Double> comparisonList = new ConcurrentLinkedQueue<Double>();
		comparisonList.add(0.0);

		addAndRemoveFromList(list, inserts, deletes, continueRun, threadPoolSize, numberInserts, numberDeletes, comparisonList, totalListOperations);
		printSummaryResults(inserts, deletes);
		log.debug(comparisonList.size() + " vs " + (inserts.get() - deletes.get()));
		testResults(list, comparisonList.size()/*inserts.get() - deletes.get()*/);
	}

	private void testResults(FDListFine<Double> list, int listSize) {
		FDListFine.Element head = list.head();
		FDListFine<Double>.Cursor reader = list.reader(list.head());

		reader.next();
		boolean isHead = false;
		int count = 0;
		while (!isHead) {
			if (reader.curr().isHead()) {
				isHead = true;
			} else if (reader.curr().isDeleted()) {
				Assert.fail("We have a deleted node in the list ... ");
			} else {
				count++;
				reader.next();
				//log.debug(count + " " + reader.curr().value().doubleValue());
			}
		}

		Assert.assertEquals("Expected list size different from size: ", listSize, count);

	}

	private void printSummaryResults(AtomicInteger inserts, AtomicInteger deletes) {
		log.info("Done");
		log.info("Added " + inserts.get());
		log.info("Removed " + deletes.get());
	}

	private void addAndRemoveFromList(final FDListFine<Double> list, final AtomicInteger insertSize, final AtomicInteger removeSize,
									  final AtomicBoolean continueRun, int threadPoolSize,
									  final int numberInserts, final int numberDeletes, final Queue<Double> comparisonList, final int totalListOperations) {
		// Create the ThreadPool
		final ExecutorService tpe = Executors.newFixedThreadPool(threadPoolSize);
		for (int i = 0; i < threadPoolSize; i++) {
			tpe.submit(new Runnable() {
				@Override
				public void run() {
					FDListFine<Double>.Cursor reader = list.reader(list.head());
					boolean add = true;
					while (continueRun.get() && !Thread.currentThread().isInterrupted()) {
						try {
							if (add) {
								for (int i = 0; i < numberInserts; i++) {
									if (i % 2 == 0) {
										reader.curr().value();
										reader.writer().insertAfter(getRandomDouble());
									} else {
										reader.curr().value();
										reader.writer().insertBefore(getRandomDouble());
									}
									insertSize.incrementAndGet();
									comparisonList.add(0.0);

								}
								add = false;

							} else {
								reader.next();
								for (int i = 0; i < numberDeletes; i++) {
									reader.curr().value();
									reader.writer().delete();
									removeSize.incrementAndGet();
									if (comparisonList.size() > 0) {
										comparisonList.remove();
									}
								}
								add = true;
							}
						} catch (IllegalStateException e) {
							log.trace("Found a deleted node ... we have to reinitialize the reader.");
							synchronized (list.head()) {
								reader = list.reader(list.head());
							}
						}
					}
				}
			});

		}

		log.debug("Sleeping...");
		try {
			Thread.sleep(totalListOperations);
		} catch (InterruptedException e) {
		}

		log.info("Shutting down pool.");
		tpe.shutdownNow();
		try {
			boolean terminated = false;
			while (!terminated) {
				terminated = tpe.awaitTermination(30, TimeUnit.SECONDS);
				log.debug("Is ThreadPoolExecutor done ... {}", terminated);
			}
		} catch (InterruptedException e) {
			log.error("Interrupted during termination of pool.");
		}

		log.debug("Pool is shut down..");

	}

	private FDListFine<Double> createListOfSizeOne() {
		// Create the list
		final FDListFine<Double> list = new FDListFine<Double>(getRandomDouble());
		FDListFine<Double>.Cursor reader = list.reader(list.head());
		reader.writer().insertAfter(getRandomDouble());
		return list;
	}

	public static Double getRandomDouble() {
		return Math.random();
	}
}
