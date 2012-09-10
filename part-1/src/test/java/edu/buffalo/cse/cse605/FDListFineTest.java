package edu.buffalo.cse.cse605;

import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: delvecchio
 * Date: 9/8/12
 * Time: 2:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class FDListFineTest {

	private Logger Log = LoggerFactory.getLogger(FDListFineTest.class);


	@Test
    public void testTailIsHiddenForOneElement(){
        String first = "first";
        FDListFine<String> list = new FDListFine<String>(first);
        FDListFine<String>.Cursor reader = list.reader(list.head());
        Assert.assertTrue(getDummyTailErrorMessage(reader), reader.curr().value().equals(first));
        reader.next();
        Assert.assertTrue(getDummyTailErrorMessage(reader), reader.curr().value().equals(first));
    }

    @Test
    public void testTailIsHiddenForMultipleElement(){
        FDListFine.Cursor reader = createNineMemberList();
        for(int i = 0; i < 100; i++){
            Assert.assertTrue(getDummyTailErrorMessage(reader), reader.curr().value() != null);
            reader.next();
        }
        for(int i = 0; i < 100; i++){
            Assert.assertTrue(getDummyTailErrorMessage(reader), reader.curr().value() != null);
            reader.prev();
        }

    }

    @Test(expected = IllegalStateException.class)
    public void testTailMaintainedForDeletes(){
        FDListFine.Cursor reader = createNineMemberList();
        for(int i = 0; i <= 7 ; i++){
            Assert.assertTrue(getDummyTailErrorMessage(reader), reader.curr().value() != null);
            reader.writer().delete();
        }
        Assert.assertTrue(getDummyTailErrorMessage(reader), reader.curr().value().equals("nine"));
        reader.next();
        Assert.assertTrue(getDummyTailErrorMessage(reader), reader.curr().value().equals("nine"));
        reader.writer().delete();
        Assert.fail("Deleted an element from a list of size one");
    }

    private String getDummyTailErrorMessage(FDListFine.Cursor reader){
        return "The dummy tail value is showing up. '" + reader.curr().value() + "'";
    }

    private FDListFine.Cursor createNineMemberList() {
        FDListFine<String> list = new FDListFine<String>("one");
        FDListFine<String>.Cursor reader = list.reader(list.head());
        reader.writer().insertAfter("two");
        reader.writer().insertBefore("three");
        reader.writer().insertAfter("four");
        reader.writer().insertBefore("five");
        reader.writer().insertAfter("six");
        reader.writer().insertBefore("seven");
        reader.writer().insertAfter("eight");
        reader.writer().insertBefore("nine");
        return reader;
    }

    @Test
    public void testCircularitySizeOne() throws Exception {
        String first = "first";
        String[] elements = {first};

        FDListFine<String> list = new FDListFine<String>(first);

        FDListFine<String>.Cursor reader = list.reader(list.head());

        assertListSame(reader, elements);
    }

    @Test
    public void testInsertionAfter() throws Exception {

        String first = "first";
        String second = "second";
        String[] elements = {first, second};

        FDListFine<String> list = new FDListFine<String>(first);

        FDListFine<String>.Cursor reader = list.reader(list.head());

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

        FDListFine<String> list = new FDListFine<String>(first);

        FDListFine<String>.Cursor reader = list.reader(list.head());

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

        FDListFine<String> list = new FDListFine<String>(first);

        FDListFine<String>.Cursor reader = list.reader(list.head());
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

        FDListFine<String> list = new FDListFine<String>(first);

        FDListFine<String>.Cursor reader = list.reader(list.head());
        reader.writer().insertAfter(second);
        reader.writer().insertBefore(third);

        Assert.assertEquals(first, reader.curr().value());
        reader.next();
        Assert.assertEquals(second, reader.curr().value());
        reader.prev();
        Assert.assertEquals(first, reader.curr().value());
    }

    private void assertListSame(FDListFine<String>.Cursor reader, String[] expected) {
        for (String element : expected) {
            Assert.assertEquals(reader.curr().value(), element);

            reader.next();
        }

        // assert the circularity
        Assert.assertEquals(reader.curr().value(), expected[0]);
    }

	@Test
	public void testConcurrentAdd()
	{

		List<Runnable> runnables = new ArrayList<Runnable>();

		final FDListFine<String> list = new FDListFine<String>("head");
		final FDListFine<String>.Cursor reader = list.reader(list.head());

		final int threadCount = 10;
		final int inserts = 10;
		for (int i = 0; i < threadCount; i++)
		{
			final int p = i;

			final Runnable r = new Runnable()
			{
				@Override
				public void run()
				{
					FDListFine<String>.Cursor reader = list.reader(list.head());

					for(int j = 0; j < inserts; j++)
					{
						reader.writer().insertAfter(p + ":" + j);
						reader.next();
					}
				}
			};

			runnables.add(r);
		}

		Set<Thread> threads = new HashSet<Thread>();

		for (Runnable r : runnables)
		{
			Thread th = new Thread(r);
			th.start();
			threads.add(th);
		}

		boolean threadsAlive = true;
		while(threads.size() > 0 && threadsAlive)
		{
			threadsAlive = false;
			for(Thread th : threads)
			{
				if(th.isAlive())
				{
					threadsAlive = true;
					break;
				}
			}
		}

		try
		{
			Thread.sleep(10000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		printList(list.reader(list.head()));
		long listCount = countList(list.reader(list.head()));
		assertEquals(threadCount * inserts + 1, listCount);
	}

	private long countList(FDListFine<String>.Cursor reader)
	{
		long listLength = 0;
		do
		{
			listLength++;
			reader.next();
		} while(!reader.curr().isHead());
		Log.debug("List has {} items in it", listLength);
		return listLength;
	}

	private void printList(FDListFine<String>.Cursor reader)
	{
		do
		{
			Log.debug("List Item: {}", reader.curr().value());
			reader.next();
		} while(!reader.curr().isHead());
	}

}
