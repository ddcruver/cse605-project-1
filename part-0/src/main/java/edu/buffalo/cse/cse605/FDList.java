package edu.buffalo.cse.cse605;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 8/29/12
 * Time: 6:26 PM
 */
public class FDList<T> {
	private Element head;

	public FDList(T v) {
		head = new Element(v);
	}

	public Element head() {
		return head;
	}

	public Cursor<T> reader(Element from) {
		return new Cursor(from);
	}

	public class Writer<T> {
		private final Element currentElement;
		private final Cursor cursor;

		public Writer(Cursor cursor) {
			this.cursor = cursor;
			this.currentElement = cursor.curr();
		}


		public boolean delete() {
			currentElement.delete();
			cursor.next();
			return true;
		}

		public boolean insertBefore(T val) {
			Element newElement = new Element(val, currentElement.getPrev(), currentElement);
			newElement.adjustNeighbors();
			return true;
		}

		public boolean insertAfter(T val) {
			Element newElement = new Element(val, currentElement, currentElement.getNext());
			newElement.adjustNeighbors();
			return true;
		}
	}

	public class Cursor<T> {
		private Element currentElement;

		private Cursor(Element currentElement) {
			this.currentElement = currentElement;
		}

		public Element curr() {
			return currentElement;
		}

		public void next() {
			currentElement = curr().getNext();
		}

		public void prev() {
			currentElement = curr().getPrev();
		}

		public Writer writer() {
			return new Writer(this);
		}
	}

	public class Element<T> implements DList.Element {
		private Element prev;
		private Element next;

		private T value;

		public Element(T value) {
			this.value = value;
			prev = this;
			next = this;
		}

		public Element(T value, Element prev, Element next) {
			this.value = value;
			this.prev = prev;
			this.next = next;
		}

		private void adjustNeighbors() {
			prev.setNext(this);
			next.setPrev(this);
		}

		private Element getNext() {
			return next;
		}

		private void setNext(Element next) {
			this.next = next;
		}

		private Element getPrev() {
			return prev;
		}

		private void setPrev(Element prev) {
			this.prev = prev;
		}

		public T value() {
			return value;
		}

		private void delete() {
			prev.setNext(next);
			next.setPrev(prev);
		}
	}
}

