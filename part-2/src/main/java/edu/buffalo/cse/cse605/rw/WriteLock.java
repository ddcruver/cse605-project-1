package edu.buffalo.cse.cse605.rw;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/19/12
 * Time: 10:07 AM
 */
public class WriteLock extends AbstractLock {
	private final AbstractLock outerLock;

	WriteLock(ReadWriteLock readWriteLock, AbstractLock outerLock) {
		super(readWriteLock);
		this.outerLock = outerLock;
	}

	@Override
	void assureIsWriterLock() {
		return;
	}

	@Override
	void assureResourcesAllocated() {
		super.assureResourcesAllocated();    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	void acquireLock(boolean b) {
		super.acquireLock(b);    //To change body of overridden methods use File | Settings | File Templates.
	}

	void releaseLock() {

//		super.releaseLock();    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	boolean canContinue(boolean topOfQueue) {
		return super.canContinue(topOfQueue);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	boolean isWriter() {
		return true;
	}
}
