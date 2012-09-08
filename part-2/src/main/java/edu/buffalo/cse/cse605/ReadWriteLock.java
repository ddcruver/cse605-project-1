package edu.buffalo.cse.cse605;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/3/12
 * Time: 5:58 PM
 */
public class ReadWriteLock {

	private AtomicInteger outstandingReadLocks = new AtomicInteger(0);
	private AtomicBoolean currentlyWriting = new AtomicBoolean(false);
	private AtomicInteger numberOfWritersWaiting = new AtomicInteger(0);

	private Queue<Lock> pendingLocks = new LinkedList<Lock>();

	private synchronized void flushQueue() {
		while (pendingLocks.peek().canContinue(true)) {
			pendingLocks.poll().resume();
		}
	}

	public Lock getReadLock() {
		return new ReadLock();
	}

	public Lock getWriteLock() {
		return new WriteLock();
	}

	public class ReadLock extends Lock {

		@Override
		protected void releaseResources() {
			outstandingReadLocks.decrementAndGet();
			flushQueue();
		}

		@Override
		protected void acquireResources() {
			outstandingReadLocks.incrementAndGet();
		}

		@Override
		protected boolean canContinue(boolean topOfQueue) {
			return (topOfQueue || numberOfWritersWaiting.get() == 0) && !currentlyWriting.get();
		}
	}

	public class WriteLock extends Lock {

		@Override
		protected void releaseResources() {
			boolean previousValue = currentlyWriting.getAndSet(false);

			if (!previousValue) {
				throw new IllegalStateException("Trying to release write lock that doesn't exist!");
			}

		}

		@Override
		protected void acquireResources() {
			boolean updateWriteStatus = currentlyWriting.compareAndSet(false, true);

			if (updateWriteStatus) {
				throw new IllegalStateException("Multiple concurrent writes!");
			}

			numberOfWritersWaiting.decrementAndGet();
		}

		@Override
		public void acquire() throws InterruptedException {
			numberOfWritersWaiting.incrementAndGet();

			super.acquire();
		}

		@Override
		protected boolean canContinue(boolean topOfQueue) {
			return outstandingReadLocks.get() == 0 && !currentlyWriting.get();
		}
	}

	public abstract class Lock {
		private Thread thread;
		private boolean allocatedResources = false;

		/**
		 * Acquires the requested lock, blocking if necessary.
		 *
		 * @throws InterruptedException If thread was interrupted
		 */
		public void acquire() throws InterruptedException {
			synchronized (ReadWriteLock.this) {
				while (!canContinue(false) && !allocatedResources) {
					sleep();
				}

				assureResourcesAcquired();

			}
		}

		private synchronized void assureResourcesAcquired() {
			if (!allocatedResources) {
				acquireResources();
				allocatedResources = true;
			}
		}

		/**
		 * Releases control of this lock.
		 */
		public void release() {
			synchronized (ReadWriteLock.this) {
				releaseResources();
				flushQueue();
			}
		}

		protected abstract void releaseResources();

		protected void sleep() throws InterruptedException {
			thread = Thread.currentThread();
			pendingLocks.add(this);
			wait();
		}

		private void resume() {
			acquireResources();
			awake();
		}

		protected abstract void acquireResources();

		private void awake() {
			thread.notify();
		}

		protected abstract boolean canContinue(boolean topOfQueue);
	}
}
