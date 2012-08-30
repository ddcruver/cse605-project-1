package edu.buffalo.cse.cse605;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 8/29/12
 * Time: 10:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class FDListTest {

	@Test
	public void testCircularitySizeOne() throws Exception {
		String first = "first";
		String[] elements = {first};

		FDList<String> list = new FDList<String>(first);

		FDList<String>.Cursor reader = list.reader(list.head());

		assertListSame(reader, elements);
	}

	@Test
	public void testFirstInsertion() throws Exception {
		String first = "first";
		String second = "second";
		String[] elements = {first, second};

		FDList<String> list = new FDList<String>(first);

		FDList<String>.Cursor reader = list.reader(list.head());

		reader.writer().insertAfter(second);

		reader = list.reader(list.head());

		assertListSame(reader, elements);
	}

	private void assertListSame(FDList<String>.Cursor reader, String[] expected) {
		for (String element : expected) {
			Assert.assertEquals(reader.curr().value(), element);

			reader.next();
		}

		// assert the circularity
		Assert.assertEquals(reader.curr().value(), expected[0]);
	}
}
