package edu.buffalo.cse.cse605;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 8/29/12
 * Time: 6:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class FDList<T> {
	private Element head;

	public FDList(T v) {
		head = new Element(v);
	}

	public Element head() {
		return head;
	}

	public Cursor reader(Element from) {
		return new Cursor(from);
	}

	public class Writer {


		public void delete() {

		}

		public boolean insertBefore(T val) {
			return false;
		}

		public boolean insertAfter(T val) {
			return false;
		}
	}

	public class Cursor {
		private Element currentElement;

		private Cursor(Element currentElement) {
			this.currentElement = currentElement;
		}

		public Element curr() {
			return null;
		}

		public void next() {
			currentElement = curr().next;
		}

		public void prev() {
			currentElement = curr().prev;
		}

		public Writer writer() {
			return null;
		}
	}

	public class Element {
		private Element prev;
		private Element next;

		private T value;

		public Element(T value) {
			this.value = value;
			prev = next = this;
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
	}

}
