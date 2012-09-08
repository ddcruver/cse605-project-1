package edu.buffalo.cse.cse605;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/8/12
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class FDCoarse<T> //implements DList<T>
{
	static class Element<T> implements DList.Element {
		T value;

		public T value() {
			return value;
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

	//@Override
	//public Cursor reader(Element from)
	//{
	//	return null;  //To change body of implemented methods use File | Settings | File Templates.
	//}
}
