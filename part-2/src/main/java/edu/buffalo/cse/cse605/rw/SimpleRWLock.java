package edu.buffalo.cse.cse605.rw;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/22/12
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleRWLock implements java.util.concurrent.locks.ReadWriteLock {
	private AtomicInteger locksOutstanding = new AtomicInteger();
	private AtomicBoolean currentlyWriting = new AtomicBoolean();
	private AtomicInteger writersWaiting = new AtomicInteger();

	private synchronized void lockReadLock() {
		boolean locked = false;

		while (!locked) {
			if (locksOutstanding.get() == 0 || (!currentlyWriting.get() && !(writersWaiting.get() == 0))) {
				locksOutstanding.incrementAndGet();
				locked = true;
			} else {
				try {
					this.wait(5000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private synchronized void lockWriteLock() {
		boolean locked = false;
		writersWaiting.incrementAndGet();

		while (!locked) {
			if (locksOutstanding.get() == 0) {
				locksOutstanding.incrementAndGet();
				currentlyWriting.set(true);
				writersWaiting.decrementAndGet();
				locked = true;
			} else {
				try {
					this.wait(5000);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	private synchronized void unlockWriteLock() {
		boolean oldStatus = currentlyWriting.getAndSet(false);
		assert oldStatus;

		locksOutstanding.decrementAndGet();

		this.notifyAll();
	}

	private synchronized void unlockReadLock() {
		locksOutstanding.decrementAndGet();

		this.notifyAll();
	}

	private Map<Thread, Lock> outstandingLockMap = new ConcurrentHashMap<Thread, Lock>();

	private abstract class MyLock implements Lock {
		protected abstract void reallyLock();

		protected abstract void reallyUnlock();

		private int numberGivenOut = 0;

		@Override
		public void lock() {
			if (numberGivenOut++ == 0) {
				reallyLock();
			}
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean tryLock() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void unlock() {
			if (--numberGivenOut == 0) {
				reallyUnlock();
			}
		}

		@Override
		public Condition newCondition() {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Lock readLock() {
		Lock lock = outstandingLockMap.get(Thread.currentThread());
		if (lock == null) {
			lock = new MyLock() {
				@Override
				protected void reallyLock() {
					lockReadLock();
				}

				@Override
				protected void reallyUnlock() {
					unlockReadLock();
					outstandingLockMap.remove(Thread.currentThread());
				}
			};
			outstandingLockMap.put(Thread.currentThread(), lock);
		}
		return lock;
	}

	@Override
	public Lock writeLock() {
		Lock lock = outstandingLockMap.get(Thread.currentThread());
		if (lock == null) {
			lock = new MyLock() {
				@Override
				protected void reallyLock() {
					lockWriteLock();
				}

				@Override
				protected void reallyUnlock() {
					unlockWriteLock();
					outstandingLockMap.remove(Thread.currentThread());
				}
			};
			outstandingLockMap.put(Thread.currentThread(), lock);
		}
		return lock;
	}
}
