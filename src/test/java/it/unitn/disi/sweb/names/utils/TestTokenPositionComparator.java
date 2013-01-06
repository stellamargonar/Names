package it.unitn.disi.sweb.names.utils;

import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestTokenPositionComparator extends TestCase {

	private TokenPositionComparator comparator;

	@Override
	@Before
	public void setUp() throws Exception {
		comparator = new TokenPositionComparator();
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testCompareNameToken() {
		NameToken n1 = new NameToken();
		n1.setPosition(0);

		NameToken n2 = new NameToken();
		n2.setPosition(0);

		assertEquals(0, comparator.compare(n1, n2));

		n1.setPosition(0);
		n2.setPosition(1);
		assertEquals(-1, comparator.compare(n1, n2));

		n1.setPosition(1);
		n2.setPosition(0);
		assertEquals(1, comparator.compare(n1, n2));

		n1.setPosition(-1);
		n2.setPosition(1);
		assertEquals(-2, comparator.compare(n1, n2));
	}

	@Test
	public final void testCompareTriggerToken() {
		TriggerWordToken t1 = new TriggerWordToken();
		TriggerWordToken t2 = new TriggerWordToken();

		t1.setPosition(0);
		t2.setPosition(0);

		assertEquals(0, comparator.compare(t1, t2));

		t1.setPosition(0);
		t2.setPosition(1);
		assertEquals(-1, comparator.compare(t1, t2));

		t1.setPosition(1);
		t2.setPosition(0);
		assertEquals(1, comparator.compare(t1, t2));

		t1.setPosition(-1);
		t2.setPosition(1);
		assertEquals(-2, comparator.compare(t1, t2));

	}

	@Test(expected = ClassCastException.class)
	public final void testCompareOther() {
		Integer i1 = 4;
		Integer i2 = 3;
		try {
			comparator.compare(i1, i2);
			fail("Expected Class Cast exception");
		} catch (ClassCastException ex) {

		}

	}

}
