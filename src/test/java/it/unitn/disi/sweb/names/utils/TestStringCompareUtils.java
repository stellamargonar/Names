package it.unitn.disi.sweb.names.utils;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStringCompareUtils {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testLengthDifference() {
		assertEquals(Integer.MAX_VALUE,
				StringCompareUtils.lengthDifference(null, null));
		assertEquals(Integer.MAX_VALUE,
				StringCompareUtils.lengthDifference("string", null));
		assertEquals(Integer.MAX_VALUE,
				StringCompareUtils.lengthDifference(null, "string"));

		assertEquals(0, StringCompareUtils.lengthDifference("", ""));
		assertEquals(4, StringCompareUtils.lengthDifference("", "test"));
		assertEquals(4, StringCompareUtils.lengthDifference("test", ""));
		assertEquals(0, StringCompareUtils.lengthDifference("test", "test"));
		assertEquals(3, StringCompareUtils.lengthDifference("testing", "test"));
		assertEquals(3, StringCompareUtils.lengthDifference("test", "testing"));

	}

}
