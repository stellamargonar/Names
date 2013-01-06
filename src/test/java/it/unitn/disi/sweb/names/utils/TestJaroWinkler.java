package it.unitn.disi.sweb.names.utils;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestJaroWinkler extends TestCase {

	JaroWinkler comparator;

	@Override
	@Before
	public void setUp() throws Exception {
		comparator = JaroWinkler.getInstance();
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetSimilarity() {
		test("ciao", "cio", 0.7, 1);
		test("Fausto", "Tesi", 0.0, 0.5);
		test("Fausto", "Fasuto", 0.7, 1);
		test("Cocomero", "Condor", 0.5, 0.8);
		test("abc", "def", 0.0, 0.3);

		test("Lago di Garda", "Lake of Garda", 0.5, 1);
		test("Fausto", "Fausto", 1.0, 1.0);
		test("", "aaa", 0.0, 0.0);
		test("aaa", "", 0.0, 0.0);

		test("", "", 1.0, 1.0);
	}

	private void test(String name1, String name2, double minvalue,
			double maxvalue) {
		double returned = comparator.getSimilarity(name1, name2);
		System.out.println(returned);
		assertTrue(minvalue <= returned && returned <= maxvalue);
	}

}
