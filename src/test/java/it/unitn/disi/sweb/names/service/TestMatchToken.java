package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@Transactional(rollbackFor = Throwable.class)
public class TestMatchToken extends TestCase {

	/*
	 * TODO aggiungere la traduzione dei nomi
	 */
	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;

	@Autowired
	NameMatch nameMatch;
	private EType etype;

	@Before
	@Override
	public void setUp() throws Exception {
		etype = etypeManager.getEtype(EtypeName.LOCATION);

		NamedEntity e1 = entityManager.createEntity(etype,
				"http://it.wikipedia.org/wiki/Piazza_San_Pietro");
		nameManager.createFullName("Piazza San Pietro", e1);

		NamedEntity e2 = entityManager.createEntity(etype,
				"http://en.wikipedia.org/wiki/Saint_Peter's_Square");
		nameManager.createFullName("Saint Peter Square", e2);
	}
	@After
	@Override
	public void tearDown() throws Exception {

	}

	/**
	 * Test method for
	 * {@link it.unitn.disi.sweb.names.service.NameMatch#tokenAnalysis(java.lang.String, java.lang.String, it.unitn.disi.sweb.names.model.EType)}
	 * .
	 */

	@Test
	public void testMatchToken1() {
		double result = nameMatch.tokenAnalysis("test", "test", null);
		System.out.println(result);
		assertTrue(result != 0.0);
	}

	@Test
	public void testMatchToken2() {
		double result = nameMatch.tokenAnalysis("New York", "New York City",
				etype);
		System.out.println(result);

		assertTrue(result > 0.0);
	}

	@Test
	public void testMatchToken3() {
		double result = nameMatch.tokenAnalysis("Piazza San Pietro",
				"San Pietro in Vincoli", etype);
		System.out.println(result);

		assertTrue(result > 0.0);
	}

	@Test
	public void testMatchToken4() {
		double result = nameMatch.tokenAnalysis("San Pietro in Vincoli",
				"Piazza San Pietro", etype);
		System.out.println(result);

		assertTrue(result > 0.0);
	}

	@Test
	public void testMatchToken5() {
		double result = nameMatch.tokenAnalysis("Garda Lake",
				"Piazza San Pietro", etype);
		System.out.println(result);

		assertEquals(0.0, result);
	}
	@Test
	public void testMatchToken6() {
		double result = nameMatch.tokenAnalysis("P.zza S. Pietro",
				"Piazza San Pietro", etype);
		System.out.println(result);

		assertTrue(result > 0.0);
	}

	@Test
	public void testMatchToken7() {
		double result = nameMatch.tokenAnalysis("Saint Peter Square",
				"Piazza San Pietro", etype);
		System.out.println(result);

		assertTrue(result > 0.0);
	}

	@Test
	public void testMatchToken8() {
		double result = nameMatch.tokenAnalysis("S. Peter Sq.",
				"Piazza San Pietro", etype);
		System.out.println(result);

		assertTrue(result > 0.0);
	}

	@Test
	public final void testTokenAnalysis() {

		String name1 = "Garda See";
		String name2 = "Garda Loke";

		double similarity = nameMatch.tokenAnalysis(name1, name2, etype);
		System.out.println(similarity);

		assertTrue(similarity >= 0.5);
	}
}
