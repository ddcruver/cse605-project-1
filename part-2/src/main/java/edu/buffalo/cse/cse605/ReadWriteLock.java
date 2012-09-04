package edu.buffalo.cse.cse605;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/3/12
 * Time: 5:58 PM
 */
public class ReadWriteLock {

	private AtomicInteger outstandingReadLocks = new AtomicInteger(0);
	private boolean currentlyWriting = false;
//	private >

	public class ReadLock {
		public void acquire() throws InterruptedException {
			synchronized (Lock.this) {
				while(currentlyWriting) {
					blockedReadingThreads.add(Thread.currentThread());
					this.wait();
				}

				blockedReadingThreads.remove(Thread.currentThread());

				outstandingReadLocks.incrementAndGet();
			}
		}

		public void release() {
			synchronized (Lock.this) {
				if(outstandingReadLocks.decrementAndGet() == 0) {

				}
			}
		}
	}

	public class WriteLock {

	}
}
