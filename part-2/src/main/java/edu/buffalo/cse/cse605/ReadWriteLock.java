package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

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
	private transient final Logger Log = LoggerFactory.getLogger(ReadWriteLock.class);

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
			Log.debug("Allocated resources for reading for thread {}", Thread.currentThread().getName());
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
<<<<<<< HEAD
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
=======
			boolean successAcquiringLock = currentlyWriting.compareAndSet(false, true);

			if (successAcquiringLock) {
				AtomicBoolean writerWaiting = new AtomicBoolean(false);
				throw new IllegalStateException("Multiple concurrent writes!");
			}

			Log.debug("Allocated resources for writing for thread {}", Thread.currentThread().getName());
>>>>>>> e8410ecc21bdf53353b4edd29832ab195b743856
		}

		@Override
		protected boolean canContinue(boolean topOfQueue) {
			return outstandingReadLocks.get() == 0 && !currentlyWriting.get();
		}
	}

	public abstract class Lock {
		private Thread thread;
		private AtomicBoolean allocatedResources = new AtomicBoolean(false);

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

				Log.debug("Lock {} acquired by thread {}.", this.getClass().getCanonicalName(), Thread.currentThread().getName());

				assureResourcesAcquired();

			}
		}

		/**
		 This is required because we need to allocate resources for a lock that has blocked BEFORE waking it, because you cannot guarantee that waking a thread, and it claiming its resources is atomic. Otherwise our notion of fairness could be violated by the following sequence: <br />
		 <br />
		 (1) Reader Lock A acquired<br />
		 (2) Writer Lock B attempted -- is not available due to reader outstanding. blocks.<br />
		 (3) Reader Lock A returned<br />
		 (4) Writer Lock B is awoken<br />
		 (5) Writer Lock C is acquired (Lock B did not claim it's resources yet)<br />
		 (6) Writer Lock B is blocked -- our fairness is broken<br />
		 <br />
		 This is solved by allocating the lock's resources before waking it (within the critical section of code). Therefore, there are two paths to acquire the lock resources, and it should only be done once.
		 */
		private void assureResourcesAcquired() {
			// if the resources are allocated already (ie. it didn't block), continue, else allocate first
			if(allocatedResources.compareAndSet(false, true)) {

				acquireResources();
			}
		}

		/**
		 * Releases control of this lock.
		 */
		public void release() {
			synchronized (ReadWriteLock.this) {
				Log.debug("Releasing lock {} by thread {}.", this.getClass().getCanonicalName(), Thread.currentThread().getName());
				releaseResources();
				flushQueue();
			}
		}

		protected abstract void releaseResources();

		protected void sleep() throws InterruptedException {
			Log.debug("Lock {} not available for thread {}, sleeping.", this.getClass().getCanonicalName(), Thread.currentThread().getName());

			thread = Thread.currentThread();
			pendingLocks.add(this);
			wait();
		}

		private void resume() {
			Log.debug("Acquiring resources & resuming thread {} -- ready for lock acquisition.", Thread.currentThread().getName());
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
