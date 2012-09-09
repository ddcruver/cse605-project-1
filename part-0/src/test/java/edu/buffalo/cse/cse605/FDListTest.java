package edu.buffalo.cse.cse605;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 8/29/12
 * Time: 10:44 PM
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
	public void testInsertionAfter() throws Exception {

		String first = "first";
		String second = "second";
		String[] elements = {first, second};

		FDList<String> list = new FDList<String>(first);

		FDList<String>.Cursor reader = list.reader(list.head());

		reader.writer().insertAfter(second);

		reader = list.reader(list.head());

		assertListSame(reader, elements);
	}

	@Test
	public void testInsertionBefore() throws Exception {

		String first = "first";
		String second = "second";
		String third = "third";
		String[] elements = {first, third, second};

		FDList<String> list = new FDList<String>(first);

		FDList<String>.Cursor reader = list.reader(list.head());

		reader.writer().insertAfter(second);
		reader.next();
		reader.writer().insertBefore(third);

		reader = list.reader(list.head());

		assertListSame(reader, elements);
	}

	@Test
	public void testDeletion() throws Exception {
		String first = "first";
		String second = "second";
		String[] elements = {first};

		FDList<String> list = new FDList<String>(first);

		FDList<String>.Cursor reader = list.reader(list.head());
		reader.writer().insertAfter(second);
		reader.next();

		reader.writer().delete();

		assertListSame(reader, elements);
	}

	@Test
	public void testCursorPrevious() throws Exception {
		String first = "first";
		String second = "second";
		String third = "third";

		FDList<String> list = new FDList<String>(first);

		FDList<String>.Cursor reader = list.reader(list.head());
		reader.writer().insertAfter(second);
		reader.writer().insertBefore(third);

		Assert.assertEquals(first, reader.curr().value());
		reader.next();
		Assert.assertEquals(second, reader.curr().value());
		reader.prev();
		Assert.assertEquals(first, reader.curr().value());
	}

	private void assertListSame(FDList<String>.Cursor reader, String[] expected) throws InterruptedException {
		for (String element : expected) {
			Assert.assertEquals(reader.curr().value(), element);

			reader.next();
		}

		// assert the circularity
		Assert.assertEquals(reader.curr().value(), expected[0]);
	}


}
