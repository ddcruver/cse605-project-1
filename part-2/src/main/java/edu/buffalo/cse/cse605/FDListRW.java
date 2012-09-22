package edu.buffalo.cse.cse605;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/9/12
 * Time: 2:55 PM
 */

public class FDListRW<T> extends FDList<T> {
	private final java.util.concurrent.locks.ReadWriteLock readWriteLock;

	public FDListRW(T v) throws InterruptedException {
		super(v);

		readWriteLock = new ReentrantReadWriteLock();
	}

	@Override
	protected Writer createWriter(Cursor from) throws InterruptedException {
		Lock lock = readWriteLock.readLock();
		lock.lock();

		try {
			return new WriterRW(from);
		} finally {
			lock.unlock();
		}
	}

	@Override
	protected Cursor createCursor(Element from) throws InterruptedException {
		Lock lock = readWriteLock.readLock();
		lock.lock();

		try {
			return new CursorRW(from);
		} finally {
			lock.unlock();
		}
	}

	@Override
	protected Element createElement(T val) throws InterruptedException { // this is only used in the constructor, no locks req
		return new ElementRW(val);
	}

	@Override
	protected Element createElement(T val, Element prev, Element next) throws InterruptedException {
		Lock lock = readWriteLock.readLock();

		lock.lock();

		try {
			return new ElementRW(val, prev, next);
		} finally {
			lock.unlock();
		}
	}

	public class WriterRW extends Writer {

		@Override
		public boolean delete() throws InterruptedException {
			Lock lock = readWriteLock.writeLock();

			lock.lock();

			try {
				if (currentElement.isDeleted()) {
					throw new IllegalStateException("Trying to use a deleted element!");
				}

				return super.delete();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean insertBefore(T val) throws InterruptedException {
			Lock lock = readWriteLock.writeLock();

			lock.lock();

			try {
				if (currentElement.isDeleted()) {
					throw new IllegalStateException("Trying to use a deleted element!");
				}
				return super.insertBefore(val);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean insertAfter(T val) throws InterruptedException {
			Lock lock = readWriteLock.writeLock();

			lock.lock();

			try {
				if (currentElement.isDeleted()) {
					throw new IllegalStateException("Trying to use a deleted element!");
				}
				return super.insertAfter(val);
			} finally {
				lock.unlock();
			}
		}

		protected WriterRW(Cursor cursor) throws InterruptedException {
			super(cursor);
		}
	}

	public class ElementRW extends Element {

		protected ElementRW(T value) {
			super(value);
		}

		protected ElementRW(T value, Element prev, Element next) {
			super(value, prev, next);
		}

		@Override
		protected Element getNext() throws InterruptedException {
			Lock lock = readWriteLock.readLock();

			lock.lock();

			try {
				if (isDeleted()) {
					throw new IllegalStateException("Trying to use a deleted element!");
				}
				return super.getNext();
			} finally {
				lock.unlock();
			}
		}

		@Override
		protected Element getPrev() throws InterruptedException {
			Lock lock = readWriteLock.readLock();

			lock.lock();

			try {
				if (isDeleted()) {
					throw new IllegalStateException("Trying to use a deleted element!");
				}
				return super.getPrev();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public T value() throws InterruptedException {
			Lock lock = readWriteLock.readLock();

			lock.lock();

			try {
				if (isDeleted()) {
					throw new IllegalStateException("Trying to use a deleted element!");
				}
				return super.value();
			} finally {
				lock.unlock();
			}
		}
	}

	public class CursorRW extends Cursor {

		protected CursorRW(Element currentElement) {
			super(currentElement);
		}

		@Override
		public Element curr() throws InterruptedException {
			Lock lock = readWriteLock.readLock();
			lock.lock();

			try {
				if (currentElement.isDeleted()) {
					throw new IllegalStateException("Trying to use a deleted element!");
				}
				return super.curr();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void next() throws InterruptedException {
			Lock lock = readWriteLock.readLock();

			lock.lock();

			try {
				if (currentElement.isDeleted()) {
					throw new IllegalStateException("Trying to use a deleted element!");
				}
				super.next();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void prev() throws InterruptedException {
			Lock lock = readWriteLock.readLock();

			lock.lock();

			try {
				if (currentElement.isDeleted()) {
					throw new IllegalStateException("Trying to use a deleted element!");
				}
				super.prev();
			} finally {
				lock.unlock();
			}
		}
	}
}
