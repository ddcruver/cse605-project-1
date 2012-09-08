package edu.buffalo.cse.cse605;

import org.junit.Test;

import java.util.concurrent.Semaphore;


/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/5/12
 * Time: 12:36 PM
 */
public class ReadWriteLockTest {

	@Test
	public void testReadWriteRead() throws Exception {

	}



	private class LockInstance {
		private State state = State.NOT_RUNNING;
		private final ReadWriteLock.Lock lockInstance;

		private LockInstance(ReadWriteLock.Lock lockInstance) {
			this.lockInstance = lockInstance;
		}
	}
	private enum State {
		NOT_RUNNING, RUNNING, DONE
	};
}
