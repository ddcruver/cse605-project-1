package edu.buffalo.cse.cse605;

import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RunnableScheduledFuture;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/8/12
 * Time: 5:49 PM
 */
public class FDCoarseTest
{
	private Logger Log = LoggerFactory.getLogger(FDCoarseTest.class);

	@Test
	public void testCreation()
	{
		String second = "second";

		FDCoarse<String> list = new FDCoarse<String>("first");
		FDCoarse<String>.Cursor reader = list.reader(list.head());
		reader.writer().insertAfter(second);
		reader.next();

		reader.writer().delete();

		assertListSame(reader, new String[]{"first"});
	}

	@Test
	public void testConcurrentAdd()
	{

		List<Runnable> runnables = new ArrayList<Runnable>();

		FDCoarse<String> list = new FDCoarse<String>("first");
		final FDCoarse<String>.Cursor reader = list.reader(list.head());

		Set<Thread> threads = new HashSet<Thread>();

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
					for(int j = 0; j < inserts; j++)
					{
						reader.next();
						reader.writer().insertAfter(p + ":" + j);
						reader.next();
					}
				}
			};

			runnables.add(r);
		}

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
					threadsAlive = true;
			}
		}

		printList(list.reader(list.head()));
		long listCount = countList(list.reader(list.head()));
		assertEquals(threadCount * inserts + 1, listCount);
	}

	private long countList(FDCoarse<String>.Cursor reader)
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

	private void printList(FDCoarse<String>.Cursor reader)
	{
		do
		{
			Log.debug("List Item: {}", reader.curr().value());
			reader.next();
		} while(!reader.curr().isHead());
	}


	private void assertListSame(FDCoarse<String>.Cursor reader, String[] expected)
	{
		for (String element : expected)
		{
			Assert.assertEquals(reader.curr().value(), element);

			reader.next();
		}

		// assert the circularity
		Assert.assertEquals(reader.curr().value(), expected[0]);
	}

}
