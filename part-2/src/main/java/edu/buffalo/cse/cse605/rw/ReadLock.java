package edu.buffalo.cse.cse605.rw;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/19/12
 * Time: 10:03 AM
 */
public class ReadLock extends AbstractLock {
	private final AbstractLock outerLock;

	protected ReadLock(ReadWriteLock readWriteLock, AbstractLock outerLock) {
		super(readWriteLock);
		this.outerLock = outerLock;
	}

	@Override
	void assureIsWriterLock() {
		outerLock.switchToWriter();
	}

	@Override
	void assureResourcesAllocated() {
		super.assureResourcesAllocated();    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	synchronized void acquireLock(boolean b) {
		if (canContinue(b)) {
			assureResourcesAllocated();
		} else {
			readWriteLock.processLock(this);
		}
	}

	@Override
	boolean canContinue(boolean topOfQueue) {
		return super.canContinue(topOfQueue);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	boolean isWriter() {
		return false;
	}
}
