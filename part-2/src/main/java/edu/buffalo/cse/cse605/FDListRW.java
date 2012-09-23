package edu.buffalo.cse.cse605;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/9/12
 * Time: 2:55 PM
 */
public class FDListRW<T> extends FDList<T> {
	private final ReadWriteLock readWriteLock = new ReadWriteLock();

	public FDListRW(T v) throws InterruptedException {
		super(v);
	}

	@Override
	protected Writer createWriter(Cursor from) throws InterruptedException {
		ReadWriteLock.Lock lock = readWriteLock.getReadLock();
		lock.acquire();

		try {
			return new WriterRW(from);
		} finally {
			lock.release();
		}
	}

	@Override
	protected Cursor createCursor(Element from) throws InterruptedException {
		ReadWriteLock.Lock lock = readWriteLock.getReadLock();
		lock.acquire();

		try {
			return new CursorRW(from);
		} finally {
			lock.release();
		}
	}

	@Override
	protected Element createElement(T val) throws InterruptedException {
		ReadWriteLock.Lock lock = readWriteLock.getReadLock();
		lock.acquire();

		try {

			return new ElementRW(val);
		} finally {
			lock.release();
		}
	}

	@Override
	protected Element createElement(T val, Element prev, Element next) throws InterruptedException {
		ReadWriteLock.Lock lock = readWriteLock.getReadLock();
		lock.acquire();

		try {
			return new ElementRW(val, prev, next);
		} finally {
			lock.release();
		}
	}

	public class WriterRW extends Writer {

		@Override
		public boolean delete() throws InterruptedException {
			ReadWriteLock.Lock lock = readWriteLock.getWriteLock();
			lock.acquire();

			try {
				return super.delete();
			} finally {
				lock.release();
			}
		}

		@Override
		public boolean insertBefore(T val) throws InterruptedException {
			ReadWriteLock.Lock lock = readWriteLock.getWriteLock();
			lock.acquire();

			try {
				return super.insertBefore(val);
			} finally {
				lock.release();
			}
		}

		@Override
		public boolean insertAfter(T val) throws InterruptedException {
			ReadWriteLock.Lock lock = readWriteLock.getWriteLock();
			lock.acquire();

			try {
				return super.insertAfter(val);
			} finally {
				lock.release();
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
			ReadWriteLock.Lock lock = readWriteLock.getReadLock();
			lock.acquire();

			try {
				return super.getNext();
			} finally {
				lock.release();
			}
		}

		@Override
		protected Element getPrev() throws InterruptedException {
			ReadWriteLock.Lock lock = readWriteLock.getReadLock();
			lock.acquire();

			try {
				return super.getPrev();
			} finally {
				lock.release();
			}
		}

		@Override
		public T value() throws InterruptedException {
			ReadWriteLock.Lock lock = readWriteLock.getReadLock();
			lock.acquire();

			try {
				return super.value();
			} finally {
				lock.release();
			}
		}
	}

	public class CursorRW extends Cursor {

		protected CursorRW(Element currentElement) {
			super(currentElement);
		}

		@Override
		public Element curr() throws InterruptedException {
			ReadWriteLock.Lock lock = readWriteLock.getReadLock();
			lock.acquire();

			try {
				return super.curr();
			} finally {
				lock.release();
			}
		}

		@Override
		public void next() throws InterruptedException {
			ReadWriteLock.Lock lock = readWriteLock.getReadLock();
			lock.acquire();

			try {
				super.next();
			} finally {
				lock.release();
			}
		}

		@Override
		public void prev() throws InterruptedException {
			ReadWriteLock.Lock lock = readWriteLock.getReadLock();
			lock.acquire();

			try {
				super.prev();
			} finally {
				lock.release();
			}
		}

		@Override
		public Writer writer() throws InterruptedException {
			ReadWriteLock.Lock lock = readWriteLock.getReadLock();
			lock.acquire();

			try {
				return super.writer();
			} finally {
				lock.release();
			}
		}
	}
}
