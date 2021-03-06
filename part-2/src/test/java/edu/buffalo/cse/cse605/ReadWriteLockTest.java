/*
package edu.buffalo.cse.cse605;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

*/
/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/7/12
 * Time: 4:45 PM
 *//*

public class ReadWriteLockTest {
	@Test
	public void testReadwriteReadConsistency() throws Exception {
		for (int i = 0; i < 1000; i++) {
			testReadWriteRead();
		}

	}

	@Test
	public void testReadWriteRead() throws Exception {
		TestSystem ts = new TestSystem(new ReadWriteLock());

		Future<Tuple<Integer, Integer>> firstRead = ts.submitReadTask();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {

		}
		Future<Tuple<Integer, Integer>> firstWrite = ts.submitWriteTask();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {

		}
		Future<Tuple<Integer, Integer>> secondRead = ts.submitReadTask();

		Assert.assertEquals("Incorrect ordering of results", firstRead.get(), new Tuple<Integer, Integer>(1, 0));
		Assert.assertEquals("Incorrect ordering of results", secondRead.get(), new Tuple<Integer, Integer>(2, 1));
		Assert.assertEquals("Incorrect ordering of results", firstWrite.get(), new Tuple<Integer, Integer>(1, 1));

		ts.shutdown();
	}

	@Test
	public void testRead() throws Exception {
		TestSystem ts = new TestSystem(new ReadWriteLock());

		Future<Tuple<Integer, Integer>> firstRead = ts.submitReadTask();

		firstRead.get();

		ts.shutdown();
	}

	private class TestSystem {
		private final AtomicInteger numberOfWritersCompleted = new AtomicInteger();
		private final AtomicInteger numberOfReadersCompleted = new AtomicInteger();

		private final ReadWriteLock masterLock;

		private ExecutorService service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 30, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

		private TestSystem(ReadWriteLock masterLock) {
			this.masterLock = masterLock;
		}

		public Future<Tuple<Integer, Integer>> submitReadTask() {
			return submitReadTask(100);
		}

		public Future<Tuple<Integer, Integer>> submitReadTask(final long numberOfLoops) {
			return service.submit(new Callable<Tuple<Integer, Integer>>() {

				@Override
				public Tuple<Integer, Integer> call() throws Exception {
					ReadWriteLock.Lock readLock = masterLock.getReadLock();

					return executeLock(readLock, numberOfLoops, true);
				}
			});
		}

		public void shutdown() throws InterruptedException {
			service.shutdown();
			service.awaitTermination(60, TimeUnit.SECONDS);
		}

		public Future<Tuple<Integer, Integer>> submitWriteTask() {
			return submitWriteTask(100);
		}

		public Future<Tuple<Integer, Integer>> submitWriteTask(final long numberOfLoops) {
			return service.submit(new Callable<Tuple<Integer, Integer>>() {

				@Override
				public Tuple<Integer, Integer> call() throws Exception {
					ReadWriteLock.Lock readLock = masterLock.getWriteLock();

					return executeLock(readLock, numberOfLoops, false);
				}
			});
		}

		private Tuple<Integer, Integer> executeLock(ReadWriteLock.Lock readLock, long numberOfLoops, boolean incrementRead) throws InterruptedException {
			readLock.acquire();

			for (int i = 0; i < numberOfLoops; i++) {
			}

			Tuple<Integer, Integer> result = new Tuple<Integer, Integer>(incrementRead ? numberOfReadersCompleted.incrementAndGet() : numberOfReadersCompleted.get(), !incrementRead ? numberOfWritersCompleted.incrementAndGet() : numberOfWritersCompleted.get());
			readLock.release();
			return result;
		}
	}

	public class Tuple<A, B> {
		private final A first;
		private final B second;

		private Tuple(A first, B second) {
			this.first = first;
			this.second = second;
		}

		public A getFirst() {
			return first;
		}

		public B getSecond() {
			return second;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			Tuple tuple = (Tuple) o;

			if (first != null ? !first.equals(tuple.first) : tuple.first != null) {
				return false;
			}
			if (second != null ? !second.equals(tuple.second) : tuple.second != null) {
				return false;
			}

			return true;
		}

		@Override
		public String toString() {
			return "Tuple{" +
					"first=" + first +
					", second=" + second +
					'}';
		}

		@Override
		public int hashCode() {
			int result = first != null ? first.hashCode() : 0;
			result = 31 * result + (second != null ? second.hashCode() : 0);
			return result;
		}
	}
}
*/
