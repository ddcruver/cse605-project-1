package edu.buffalo.cse.cse605.rw;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/19/12
 * Time: 9:47 AM
 */
public class ReadWriteLock implements java.util.concurrent.locks.ReadWriteLock {
	private Map<Thread, AbstractLock> outstandingLocks = new ConcurrentHashMap<Thread, AbstractLock>();
	private ConcurrentLinkedQueue<AbstractLock> blockedThreads = new ConcurrentLinkedQueue<AbstractLock>();
	private AtomicBoolean currentlyWriting = new AtomicBoolean(false);
	private AtomicInteger numberOfWaitingWriters = new AtomicInteger(0);
	private AtomicInteger numberOfOutstandingReaders = new AtomicInteger(0);

	@Override
	public Lock readLock() {
		AbstractLock lock = outstandingLocks.get(Thread.currentThread());

		if (lock == null) {
			lock = AbstractLock.createReadLock(this);
			outstandingLocks.put(Thread.currentThread(), lock);
		}

		return lock;
	}

	@Override
	public Lock writeLock() {
		AbstractLock lock = outstandingLocks.get(Thread.currentThread());

		if (lock == null) {
			lock = AbstractLock.createWriteLock(this);
			outstandingLocks.put(Thread.currentThread(), lock);
		} else {

			lock.assureIsWriterLock();
		}

		return lock;
	}

	AbstractLock switchToWriter(AbstractLock actualHeldLock, AbstractLock outerLock) {
		releaseLock(actualHeldLock, false);

		AbstractLock lock = new WriteLock(this, outerLock);
		lock.acquireLock(true);

		return lock;
	}

	synchronized void releaseLock(ReadLock readLock, boolean allowOthersToContinue) {
		AbstractLock lock = outstandingLocks.remove(Thread.currentThread());
		assert lock == readLock;

		int i = numberOfOutstandingReaders.decrementAndGet();
		assert i >= 0;

		if (allowOthersToContinue)
			flushQueue();
	}

	synchronized void releaseLock(WriteLock writeLock, boolean allowOthersToContinue) {
		AbstractLock lock = outstandingLocks.remove(Thread.currentThread());
		assert lock == writeLock;

		boolean wasWriting = currentlyWriting.getAndSet(false);
		assert wasWriting;

		if (allowOthersToContinue)
			flushQueue();
	}

	synchronized void processLock(AbstractLock lock) {
		if (lock.canContinue(blockedThreads.isEmpty())) {
			lock.assureResourcesAllocated();
		} else {
			if (lock instanceof WriteLock) {
				numberOfWaitingWriters.incrementAndGet(); // TODO this is a hack
			}

			blockedThreads.add(lock);
		}
	}

	synchronized void releaseLock(AbstractLock abstractLock, boolean allowOthersToContinue) {
		throw new IllegalStateException();
	}

	private synchronized void flushQueue() {
		while (!blockedThreads.isEmpty() && blockedThreads.peek().canContinue(true)) {
			blockedThreads.poll().resume();
		}
	}
}
