package edu.buffalo.cse.cse605;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/1/12
 * Time: 11:39 PM
 */
public class FDListFine<T> {
	private final Element head;

	public FDListFine(T v) {
		head = new Element(v);
	}

	public Element head() {
		return head;
	}

	public Cursor reader(Element from) {
		return new Cursor(from);
	}

	public class Writer {
		private final Element currentElement;
		private final Cursor cursor;

		public Writer(Cursor cursor) {
			this.cursor = cursor;
			this.currentElement = cursor.curr();
		}


		public void delete() {
			synchronized (currentElement) {
				if (currentElement.isDeleted()) {
					throw new IllegalStateException("The requested element was previously deleted.");
				}
				currentElement.delete();
				cursor.next();
			}
		}

		public boolean insertBefore(T val) {
			synchronized (currentElement.getPrev().next) {
				Element newElement = new Element(val, currentElement.getPrev(), currentElement);
				newElement.adjustNeighbors();
			}
			return true;
		}

		public boolean insertAfter(T val) {
			Element newElement = new Element(val, currentElement, currentElement.getNext());
			newElement.adjustNeighbors();

			return true;
		}
	}

	public class Cursor {
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

	public class Element {
		private final GuardedReference<Element> prev;
		private final GuardedReference<Element> next;

		private boolean deleted = false;

		private T value;

		public Element(T value) {
			this.value = value;
			prev = new GuardedReference<Element>(this);
			next = new GuardedReference<Element>(this);
		}

		public Element(T value, Element prev, Element next) {
			this.value = value;
			this.prev = new GuardedReference<Element>(prev);
			this.next = new GuardedReference<Element>(next);
		}

		private void adjustNeighbors() {
			synchronized (prev.get().next) {
				synchronized (prev) {
					synchronized (next) {
						synchronized (next.get().prev) {
							prev.get().setNext(this);
							next.get().setPrev(this);
						}
					}
				}
			}
		}

		private Element getNext() {
			return next.get();
		}

		private void setNext(Element next) {
			this.next.set(next);
		}

		private Element getPrev() {
			return prev.get();
		}

		private void setPrev(Element prev) {
			this.prev.set(prev);
		}

		public T value() {
			return value;
		}

		private void delete() {
			synchronized (prev.get().next) {
				synchronized (prev) {
					synchronized (next) {
						synchronized (next.get().prev) {
							synchronized (this) {
								deleted = true; // invalidates cursors pointing here
							}
							prev.get().setNext(next.get());
							next.get().setPrev(prev.get());
						}
					}
				}
			}
		}

		public boolean isDeleted() {
			return deleted;
		}
	}
}
