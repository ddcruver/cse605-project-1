package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 9/1/12
 * Time: 11:39 PM
 */
public class FDListFine<T> {
	private Logger Log = LoggerFactory.getLogger(FDListFine.class);

	private final Element head;
	private final Element tail;

	public FDListFine(T v) {
		head = new Head(v);
		tail = new Tail(v);
		head.setNext(tail);
		head.setPrev(tail);
		tail.setNext(head);
		tail.setPrev(head);
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
			while (true) {
				Element prev = currentElement.getPrev();
				Element adjustedPrev = prev;
				if (prev.isTail()) {
					adjustedPrev = currentElement;
				}

				synchronized (adjustedPrev) {
					if (prev != currentElement.getPrev() && !currentElement.getPrev().isTail()) {
						Log.trace("The previous element changed, have to reattempt lock acquisition.");
						continue;
					}
					synchronized (currentElement) {

						synchronized (currentElement.getNext()) {
							if (currentElement.isDeleted()) {
								throw new IllegalStateException("The requested element was previously deleted.");
							}
							if (currentElement.isHead()) {
								throw new IllegalStateException("Trying to delete the head.");
							}
							currentElement.delete();
							cursor.next();
						}
						break;

					}
				}
			}
		}

		public boolean insertBefore(T val) {
			while (true) {
				Element prev = currentElement.getPrev();
				Element adjustedPrev = prev;
				if (prev.isTail()) {
					adjustedPrev = currentElement;
				}

				synchronized (adjustedPrev) {
					if (prev != currentElement.getPrev() && !currentElement.getPrev().isTail()) {
						Log.trace("The previous element changed, have to reattempt lock acquisition.");
						continue;
					}

					synchronized (currentElement) {
						synchronized (currentElement.getNext()) {
							if (currentElement.isDeleted()) {
								throw new IllegalStateException("The requested element was previously deleted.");
							}
							Element newElement = new Element(val, currentElement.getPrev(), currentElement);
							newElement.adjustNeighbors();
							break;
						}
					}
				}
			}
			return true;
		}

		public boolean insertAfter(T val) {
			while (true) {
				Element prev = currentElement.getPrev();
				Element adjustedPrev = prev;
				if (prev.isTail()) {
					adjustedPrev = currentElement;
				}

				synchronized (adjustedPrev) {
					if (prev != currentElement.getPrev() && !currentElement.getPrev().isTail()) {
						Log.trace("The previous element changed, have to reattempt lock acquisition.");
						continue;
					}

					synchronized (currentElement) {
						synchronized (currentElement.getNext()) {
							if (currentElement.isDeleted()) {
								throw new IllegalStateException("The requested element was previously deleted.");
							}
							Element newElement = new Element(val, currentElement, currentElement.getNext());
							newElement.adjustNeighbors();
							break;
						}
					}
				}
			}
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
			synchronized (currentElement) {
				synchronized (currentElement.getNext()) {
					if (curr().getNext().isTail()) {
						currentElement = curr().getNext().getNext();
					} else {
						currentElement = curr().getNext();
					}
				}
			}
		}

		public void prev() {
			synchronized (currentElement) {
				synchronized (currentElement.getPrev()) {
					if (curr().getPrev().isTail()) {
						currentElement = curr().getPrev().getPrev();
					} else {
						currentElement = curr().getPrev();
					}
				}
			}
		}

		public Writer writer() {
			return new Writer(this);
		}
	}

	public class Element {
		private final AtomicReference<Element> prev;
		private final AtomicReference<Element> next;

		private boolean deleted = false;

		private T value;

		public Element(T value) {
			this.value = value;
			prev = new AtomicReference<Element>(this);
			next = new AtomicReference<Element>(this);
		}

		public Element(T value, Element prev, Element next) {
			this.value = value;
			this.prev = new AtomicReference<Element>(prev);
			this.next = new AtomicReference<Element>(next);
		}

		public boolean isTail() {
			return false;
		}

		public boolean isHead() {
			return false;
		}

		private void adjustNeighbors() {
			prev.get().setNext(this);
			next.get().setPrev(this);
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

		private boolean delete() throws IllegalStateException {
			if (getNext().isTail() && getPrev().isTail()) {
				throw new IllegalStateException("delete() operation tried to delete element from list of size one.");
			}
			if (isHead() || isTail()) {
				throw new IllegalStateException("delete() operation tried to delete head or tail element from list.");
			}
			deleted = true; // invalidates cursors pointing here
			prev.get().setNext(next.get());
			next.get().setPrev(prev.get());
			return deleted;
		}

		public boolean isDeleted() {
			return deleted;
		}
	}

	public class Tail extends Element {

		public Tail(T value) {
			super(value);
		}

		public boolean isTail() {
			return true;
		}
	}

	public class Head extends Element {

		public Head(T value) {
			super(value);
		}

		public boolean isHead() {
			return true;
		}
	}
}
