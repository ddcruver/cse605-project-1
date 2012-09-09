package edu.buffalo.cse.cse605;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 8/29/12
 * Time: 6:26 PM
 */
public class FDList<T> {
	private Element head;

	public FDList(T v) throws InterruptedException {
		head = createElement(v);
	}

	public Element head() {
		return head;
	}

	public Cursor reader(Element from) throws InterruptedException {
		return createCursor(from);
	}

	protected Element createElement(T val, Element prev, Element next) throws InterruptedException {
		return new Element(val, prev, next);
	}

	protected Element createElement(T val) throws InterruptedException {
		return new Element(val);
	}

	protected Cursor createCursor(Element from) throws InterruptedException {
		return new Cursor(from);
	}

	protected Writer createWriter(Cursor from) throws InterruptedException {
		return new Writer(from);
	}

	public class Writer {
		private final Element currentElement;
		private final Cursor cursor;

		protected Writer(Cursor cursor) throws InterruptedException {
			this.cursor = cursor;
			this.currentElement = cursor.curr();
		}


		public boolean delete() throws InterruptedException {
			currentElement.delete();
			cursor.next();
			return true;
		}

		public boolean insertBefore(T val) throws InterruptedException {
			Element newElement = createElement(val, currentElement.getPrev(), currentElement);
			newElement.adjustNeighbors();
			return true;
		}

		public boolean insertAfter(T val) throws InterruptedException {
			Element newElement = createElement(val, currentElement, currentElement.getNext());
			newElement.adjustNeighbors();
			return true;
		}
	}

	public class Cursor {
		protected Element currentElement;

		protected Cursor(Element currentElement) {
			this.currentElement = currentElement;
		}

		public Element curr() throws InterruptedException {
			return currentElement;
		}

		public void next() throws InterruptedException {
			currentElement = curr().getNext();
		}

		public void prev() throws InterruptedException {
			currentElement = curr().getPrev();
		}

		public Writer writer() throws InterruptedException {
			return createWriter(this);
		}
	}

	public class Element {
		private Element prev;
		private Element next;

		private T value;

		protected Element(T value) {
			this.value = value;
			prev = this;
			next = this;
		}

		protected Element(T value, Element prev, Element next) {
			this.value = value;
			this.prev = prev;
			this.next = next;
		}

		private void adjustNeighbors() {
			prev.setNext(this);
			next.setPrev(this);
		}

		protected Element getNext() throws InterruptedException {
			return next;
		}

		private void setNext(Element next) {
			this.next = next;
		}

		protected Element getPrev() throws InterruptedException {
			return prev;
		}

		private void setPrev(Element prev) {
			this.prev = prev;
		}

		public T value() throws InterruptedException {
			return value;
		}

		private void delete() {
			prev.setNext(next);
			next.setPrev(prev);
		}
	}
}

