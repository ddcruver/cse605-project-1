package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


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
	private final Map<Thread, Lock> executingLocks = new ConcurrentHashMap<Thread, Lock>();

	private synchronized void flushQueue() {
		while (true) {
			boolean b = !pendingLocks.isEmpty() && pendingLocks.peek().canContinue(true);
			if (b) {
				pendingLocks.poll().resume();

			} else {
				break;
			}
		}
	}

	public Lock getReadLock() {
		Lock lock = executingLocks.get(Thread.currentThread());
		if (lock == null) {
			lock = new ReadLock();
		}
		return lock;
	}

	public Lock getWriteLock() {
		Lock lock = executingLocks.get(Thread.currentThread());
		if (lock == null) {
			lock = new WriteLock();
		}
		return lock;
	}

	public class ReadLock extends Lock {

		@Override
		protected void releaseResources() {
			outstandingReadLocks.decrementAndGet();
		}

		@Override
		protected void acquireResources() {
			Log.debug("Allocated resources for reading for thread {}", Thread.currentThread().getName());
			outstandingReadLocks.incrementAndGet();
		}

		@Override
		protected boolean canContinue(boolean topOfQueue) {
			return (allocatedResources.get() || topOfQueue || numberOfWritersWaiting.get() == 0) && !currentlyWriting.get();
		}
	}

	public class WriteLock extends Lock {
		private int numberOfLocksAcquired = 0;

		@Override
		protected void releaseResources() {
			boolean previousValue = currentlyWriting.getAndSet(false);

			if (!previousValue) {
				Log.error("Trying to release invalid lock.");
				throw new IllegalStateException("Trying to release write lock that doesn't exist!");
			}

			Lock remove = executingLocks.remove(Thread.currentThread());
//			assert remove == this;

		}

		@Override
		protected void acquireResources() {
			boolean updateWriteStatus = currentlyWriting.compareAndSet(false, true);
			int previousNumberOfWriters = numberOfWritersWaiting.getAndDecrement();

			executingLocks.put(Thread.currentThread(), this);

			assert updateWriteStatus;
			assert previousNumberOfWriters > 0;

		}

		@Override
		public void acquire() throws InterruptedException {
			if (++numberOfLocksAcquired == 1) {
				numberOfWritersWaiting.incrementAndGet();
				super.acquire();
				Log.debug("Allocated resources for writing for thread {}", Thread.currentThread().getName());
			} else {
				Log.debug("Thread already acquired lock, not giving a new one.");
			}
		}

		@Override
		protected boolean canContinue(boolean topOfQueue) {
			return outstandingReadLocks.get() == 0 && (allocatedResources.get() || !currentlyWriting.get());
		}

		@Override
		public void release() {
			if (--numberOfLocksAcquired == 0) {
				super.release();
			} else {
				Log.debug("Still have outstanding locks, not releasing.");
			}
		}
	}

	public abstract class Lock {
		protected AtomicBoolean allocatedResources = new AtomicBoolean(false);

		/**
		 * Acquires the requested lock, blocking if necessary.
		 *
		 * @throws InterruptedException If thread was interrupted
		 */
		public void acquire() throws InterruptedException {
			synchronized (this) {
				while (true) {
					synchronized (ReadWriteLock.this) {
						if (canContinue(false)) {
							assureResourcesAcquired();
							pendingLocks.remove(this);
							break;
						}

						if (!pendingLocks.contains(this)) {
							pendingLocks.add(this);
						}
					}

					sleep();

				}


			}
			Log.debug("Lock {} acquired by thread {}.", this.getClass().getCanonicalName(), Thread.currentThread().getName());

		}

		/**
		 * This is required because we need to allocate resources for a lock that has blocked BEFORE waking it, because you cannot guarantee that waking a thread, and it claiming its resources is atomic. Otherwise our notion of fairness could be violated by the following sequence: <br />
		 * <br />
		 * (1) Reader Lock A acquired<br />
		 * (2) Writer Lock B attempted -- is not available due to reader outstanding. blocks.<br />
		 * (3) Reader Lock A returned<br />
		 * (4) Writer Lock B is awoken<br />
		 * (5) Writer Lock C is acquired (Lock B did not claim it's resources yet)<br />
		 * (6) Writer Lock B is blocked -- our fairness is broken<br />
		 * <br />
		 * This is solved by allocating the lock's resources before waking it (within the critical section of code). Therefore, there are two paths to acquire the lock resources, and it should only be done once.
		 */
		private void assureResourcesAcquired() {
			// if the resources are allocated already (ie. it didn't block), continue, else allocate first
			if (allocatedResources.compareAndSet(false, true)) {

				acquireResources();
			}
		}

		/**
		 * Releases control of this lock.
		 */
		public void release() {
			synchronized (ReadWriteLock.this) {
				Log.debug("Releasing lock {} by thread {}.", this.getClass().getCanonicalName(), Thread.currentThread().getName());
				releaseHeldResources();
				flushQueue();
			}
		}

		private void releaseHeldResources() {
			boolean status = allocatedResources.compareAndSet(true, false);

			if (!status) {
				Log.error("Invalid release.");
				throw new IllegalStateException("Releasing lock not held!");
			}

			releaseResources();
		}

		protected abstract void releaseResources();

		protected void sleep() throws InterruptedException {
			Log.debug("Lock {} not available for thread {}, sleeping. (#" + pendingLocks.size() + ")", this.getClass().getCanonicalName(), Thread.currentThread().getName());

			this.wait();
		}

		private void resume() {
			Log.debug("Acquiring resources & resuming thread {} -- ready for lock acquisition.", Thread.currentThread().getName());
			assureResourcesAcquired();
			awake();
		}

		protected abstract void acquireResources();

		private void awake() {
			synchronized (this) {
				this.notify();
			}
		}

		protected abstract boolean canContinue(boolean topOfQueue);
	}
}
