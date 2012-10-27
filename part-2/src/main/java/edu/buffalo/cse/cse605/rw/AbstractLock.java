package edu.buffalo.cse.cse605.rw;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/19/12
 * Time: 9:48 AM
 */
public class AbstractLock implements Lock {
	protected final ReadWriteLock readWriteLock;

	private AbstractLock actualHeldLock;
	private long numberOfAcquiredLocks = 0;

	private static AbstractLock createLock(ReadWriteLock readWriteLock, boolean writeLock) {
		AbstractLock lock = new AbstractLock(readWriteLock);

		lock.actualHeldLock = (writeLock) ? new WriteLock(readWriteLock, lock) : new ReadLock(readWriteLock, lock);

		return lock;
	}

	static AbstractLock createWriteLock(ReadWriteLock readWriteLock) {
		return createLock(readWriteLock, false);
	}

	static AbstractLock createReadLock(ReadWriteLock readWriteLock) {
		return createLock(readWriteLock, false);
	}


	protected AbstractLock(ReadWriteLock readWriteLock) {
		this.readWriteLock = readWriteLock;
	}

	@Override
	public final void lock() {
		if (numberOfAcquiredLocks++ == 0) {
			acquireLock(false);
		}
	}

	@Override
	public final void unlock() {
		if (--numberOfAcquiredLocks == 0) {
			readWriteLock.releaseLock(actualHeldLock, true);
		}
	}

	void assureIsWriterLock() {
		actualHeldLock.assureIsWriterLock();
	}

	protected final void switchToWriter() {
		actualHeldLock = readWriteLock.switchToWriter(actualHeldLock, this);
	}

	private volatile boolean allocatedResources = false;

	void assureResourcesAllocated() {
		if (actualHeldLock != null) {
			actualHeldLock.assureResourcesAllocated();
		} else {
			if (!allocatedResources) {
				allocatedResources = true;
				allocateResources();
			}
		}
	}

	protected void allocateResources() {
		throw new IllegalStateException("");
	}

	void acquireLock(boolean b) {
		if (actualHeldLock != null) {
			actualHeldLock.acquireLock(b);
		} else {
			while (true) {
				synchronized (this) {
					synchronized (readWriteLock) {
						if (allocatedResources || canContinue(false)) {
							assureResourcesAllocated();
							return;
						} else {
							inProcessOfSleeping = true;
						}
					}

					if (inProcessOfSleeping) {
						try {
							readWriteLock.processLock(this);
							this.wait();
						} catch (InterruptedException e) {
						} // who cares
					}
				}
			}
		}
	}

	void wakeUp() {

	}

	private volatile boolean inProcessOfSleeping = false;

	boolean canContinue(boolean topOfQueue) {
		return actualHeldLock.canContinue(topOfQueue);
	}

	boolean isWriter() {
		return actualHeldLock.isWriter();
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean tryLock() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException("Not implemented.");
	}


	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	void resume() {
		// TODO
	}
}
