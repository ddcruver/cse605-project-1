package edu.buffalo.cse.cse605;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/8/12
 * Time: 11:18 AM
 */
public class FDCoarse<T> //implements DList<T>
{
	static class Element<T> implements DList.Element {
		T value;

		public T value() {
			return value;
		}
	}

	static class Writer<T> implements DList.Writer
	{

		@Override
		public boolean delete()
		{
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean insertBefore(Object val)
		{
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public boolean insertAfter(Object val)
		{
			return false;  //To change body of implemented methods use File | Settings | File Templates.
		}
	}


	static class Cursor<T> implements DList.Cursor
	{
		public Cursor(Element from)
		{

		}

		@Override
		public DList.Element curr()
		{
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void prev()
		{
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public void next()
		{
			//To change body of implemented methods use File | Settings | File Templates.
		}

		@Override
		public Writer writer()
		{
			return null;  //To change body of implemented methods use File | Settings | File Templates.
		}
	}

	private Element head;

	public FDCoarse()
	{
		head = new Element();
	}

	//@Override
	public Element head()
	{
		return head;
	}

	public Cursor reader(Element from)
	{
		return new Cursor(from);
	}

}
