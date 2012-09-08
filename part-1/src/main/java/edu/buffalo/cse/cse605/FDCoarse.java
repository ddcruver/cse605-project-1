package edu.buffalo.cse.cse605;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/8/12
 * Time: 11:18 AM
 */
public class FDCoarse<T>
{
	private final Element head;
	private final Element tail;

	public FDCoarse(T v)
	{
		head = new Element(v);
		tail = new Tail(v);
		head.setNext(tail);
		head.setPrev(tail);
		tail.setNext(head);
		tail.setPrev(head);
	}

	public Element head()
	{
		return head;
	}

	public Cursor reader(Element from)
	{
		return new Cursor(from);
	}

	public class Writer
	{
		private final Element currentElement;
		private final Cursor cursor;

		public Writer(Cursor cursor)
		{
			this.cursor = cursor;
			this.currentElement = cursor.curr();
		}


		public synchronized void delete()
		{
			if (currentElement.isDeleted())
			{
				throw new IllegalStateException("The requested element was previously deleted.");
			}
			currentElement.delete();
			cursor.next();
		}

		public synchronized boolean insertBefore(T val)
		{
			Element newElement = new Element(val, currentElement.getPrev(), currentElement);
			newElement.adjustNeighbors();
			return true;
		}

		public synchronized boolean insertAfter(T val)
		{
			Element newElement = new Element(val, currentElement, currentElement.getNext());
			newElement.adjustNeighbors();
			return true;
		}
	}

	public class Cursor
	{
		private Element currentElement;

		private Cursor(Element currentElement)
		{
			this.currentElement = currentElement;
		}

		public Element curr()
		{
			return currentElement;
		}

		public void next()
		{
			if (curr().getNext().isTail())
			{
				currentElement = curr().getNext().getNext();
			} else
			{
				currentElement = curr().getNext();
			}
		}

		public void prev()
		{
			if (curr().getPrev().isTail())
			{
				currentElement = curr().getPrev().getPrev();
			} else
			{
				currentElement = curr().getPrev();
			}
		}

		public Writer writer()
		{
			return new Writer(this);
		}
	}

	public class Element
	{
		private Element prev;
		private Element next;

		private boolean deleted = false;

		private T value;

		public Element(T value)
		{
			this.value = value;
			prev = this;
			next = this;
		}

		public Element(T value, Element prev, Element next)
		{
			this.value = value;
			this.prev = prev;
			this.next = next;
		}

		public boolean isTail()
		{
			return false;
		}

		private synchronized void adjustNeighbors()
		{
			prev.setNext(this);
			next.setPrev(this);
		}

		private Element getNext()
		{
			return next;
		}

		private void setNext(Element next)
		{
			this.next = next;
		}

		private Element getPrev()
		{
			return prev;
		}

		private void setPrev(Element prev)
		{
			this.prev = prev;
		}

		public T value()
		{
			return value;
		}

		private synchronized boolean delete() throws IllegalStateException
		{
			if (getNext().isTail() && getPrev().isTail())
			{
				throw new IllegalStateException("delete() operation tried to delete element from list of size one.");
			}

			deleted = true; // invalidates cursors pointing here

			prev.setNext(next);
			next.setPrev(prev);

			return deleted;
		}

		public boolean isDeleted()
		{
			return deleted;
		}
	}

	public class Tail extends Element
	{

		public Tail(T value)
		{
			super(value);
		}

		public boolean isTail()
		{
			return true;
		}
	}
}

