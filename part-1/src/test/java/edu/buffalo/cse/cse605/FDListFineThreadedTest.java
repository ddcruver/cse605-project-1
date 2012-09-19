package edu.buffalo.cse.cse605;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: delvecchio
 * Date: 9/8/12
 * Time: 4:50 PM
 */
public class FDListFineThreadedTest {
	private final int numberOfThreads = 10;

	@Test
	public void threadAccessToListInsertAndRemoveTest() throws InterruptedException {
		addAndRemoveElements(numberOfThreads, 1000, 4, 1);
		addAndRemoveElements(numberOfThreads, 1000, 4, 2);
		addAndRemoveElements(numberOfThreads, 1000, 1, 1);
		addAndRemoveElements(numberOfThreads, 1000, 3, 4);
	}

	private void addAndRemoveElements(int threadPoolSize, int sleepTime, int numberInserts, int numberDeletes) throws InterruptedException {
		final FDListFine<Double> list = createListOfSizeOne();
		final AtomicInteger inserts = new AtomicInteger(1);
		final AtomicInteger deletes = new AtomicInteger(0);
		final AtomicBoolean continueRun = new AtomicBoolean(true);
		Queue<Double> comparisonList = new ConcurrentLinkedQueue<Double>();
		comparisonList.add(0.0);

		addAndRemoveFromList(list, inserts, deletes, continueRun, threadPoolSize, numberInserts, numberDeletes, comparisonList);
		sleepAndWakeThread(continueRun, sleepTime);
		printSummaryResults(inserts, deletes);
		System.out.println(comparisonList.size() + " vs " + (inserts.get() - deletes.get()));
		testResults(list, comparisonList.size()/*inserts.get() - deletes.get()*/);
	}

	private void testResults(FDListFine<Double> list, int listSize) {
		FDListFine.Element head = list.head();
		FDListFine<Double>.Cursor reader = list.reader(list.head());

		reader.next();
		boolean isHead = false;
		int count = 0;
		while (!isHead) {
			if (reader.curr().value().compareTo((Double) head.value()) == 0) {
				isHead = true;
			} else if (reader.curr().isDeleted()) {
				Assert.fail("We have a deleted node in the list ... ");
			} else {
				count++;
				reader.next();
				//System.out.println(count + " " + reader.curr().value().doubleValue());
			}
		}

		Assert.assertEquals("Expected list size different from size: ", listSize, count);

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
									  final int numberInserts, final int numberDeletes, final Queue<Double> comparisonList) {
		// Create the ThreadPool
		ExecutorService tpe = Executors.newFixedThreadPool(threadPoolSize);
		for (int i = 0; i < threadPoolSize; i++) {
			tpe.submit(new Runnable() {
				@Override
				public void run() {
					FDListFine<Double>.Cursor reader = list.reader(list.head());
					boolean add = true;
					while (continueRun.get()) {
						if (add) {
							for (int i = 0; i < numberInserts; i++) {
								reader.writer().insertAfter(getRandomDouble());
								insertSize.incrementAndGet();
								comparisonList.add(0.0);
							}
							add = false;

						} else {
							try {
								reader.next();
								for (int i = 0; i < numberDeletes; i++) {
									reader.writer().delete();
									removeSize.incrementAndGet();
									comparisonList.remove();
								}
								add = true;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			});

		}
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
