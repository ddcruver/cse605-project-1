package edu.buffalo.cse.cse605;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ddcruver
 * Date: 9/8/12
 * Time: 12:15 PM
 */
public class TestLogger
{
	private static final Logger log = LoggerFactory.getLogger(TestLogger.class);


	public static void main(String args[])
	{
		log.debug("Hello");
	}

}
