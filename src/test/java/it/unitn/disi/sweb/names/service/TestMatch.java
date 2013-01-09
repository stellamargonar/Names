/**
 *
 */
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

/**
 * @author stella
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@Transactional(rollbackFor = Throwable.class)
public class TestMatch extends TestCase {

	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;

	@Autowired
	NameMatch nameMatch;

	private EType etype;

	/**
	 * sets up data for testing
	 *
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		etype = etypeManager.getEtype(EtypeName.LOCATION);
	}

	private void setUpDictionaryLookup() {
		// create entity nyc
		NamedEntity nyc = entityManager.createEntity(etype,
				"http://en.wikipedia.org/wiki/New_York_City");
		nameManager.createFullName("New York", nyc);
		nameManager.createFullName("The Big Apple", nyc);

		NamedEntity nys = entityManager.createEntity(etype,
				"http://en.wikipedia.org/wiki/New_York");
		nameManager.createFullName("New York State", nys);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		// TODO remove entities or simply clear tables
	}

	/**
	 * Test method for
	 * {@link it.unitn.disi.sweb.names.service.NameMatch#stringSimilarity(java.lang.String, java.lang.String, it.unitn.disi.sweb.names.model.EType)}
	 * .
	 */
	@Test
	public final void testStringSimilarity1() {
		String name1 = null;
		String name2 = null;

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertEquals(0.0, similarity);
	}
	@Test
	public void testStringSimilarity2() {
		String name1 = null;
		String name2 = "a";

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertEquals(0.0, similarity);
	}
	@Test
	public void testStringSimilarity3() {
		String name1 = "a";
		String name2 = null;

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertEquals(0.0, similarity);
	}

	@Test
	public final void testStringSimilarity4() {
		String name1 = "";
		String name2 = "";

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertEquals(1.0, similarity);
	}

	@Test
	public final void testStringSimilarity5() {
		String name1 = "";
		String name2 = "ABCDEFG";

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertEquals(0.0, similarity);
	}

	@Test
	public final void testStringSimilarity6() {
		String name1 = "abc";
		String name2 = "def";

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertEquals(0.0, similarity);
	}

	@Test
	public final void testStringSimilarity7() {
		String name1 = "ab";
		String name2 = "abc";

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertTrue(similarity > 0.5);
	}

	@Test
	public final void testStringSimilarity8() {
		String name1 = "abc def";
		String name2 = "def abc";

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertEquals(0.0, similarity);
	}

	/**
	 * test string length difference
	 */
	@Test
	public final void testStringSimilarity9() {
		String name1 = "annalisa";
		String name2 = "anna";

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertEquals(0.0, similarity);
	}
	@Test
	public final void testStringSimilarity10() {
		String name1 = "guglielmo";
		String name2 = "pierpaolo";

		double similarity = nameMatch.stringSimilarity(name1, name2, etype);
		assertTrue(similarity < 0.6);
	}

	/**
	 * Test method for
	 * {@link it.unitn.disi.sweb.names.service.NameMatch#stringSimilarity(it.unitn.disi.sweb.names.model.FullName, it.unitn.disi.sweb.names.model.FullName, it.unitn.disi.sweb.names.model.EType)}
	 * .
	 */
	// @Test
	public final void testStringSimilarityFullName() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link it.unitn.disi.sweb.names.service.NameMatch#dictionaryLookup(java.lang.String, java.lang.String, it.unitn.disi.sweb.names.model.EType)}
	 * .
	 */
	@Test
	public final void testDictionaryLookup1() {
		setUpDictionaryLookup();

		String name1 = null;
		String name2 = null;

		double similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertEquals(0.0, similarity);
	}

	@Test
	public final void testDictionaryLookup2() {
		setUpDictionaryLookup();

		String name1 = null;
		String name2 = "test";

		double similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertEquals(0.0, similarity);

		name1 = "test";
		name2 = null;
		similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertEquals(0.0, similarity);

	}

	@Test
	public final void testDictionaryLookup3() {
		setUpDictionaryLookup();

		String name1 = "New York";
		String name2 = "The Big Apple";

		double similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertEquals(1.0, similarity);
	}

	@Test
	public final void testDictionaryLookup4() {
		setUpDictionaryLookup();

		String name1 = "New York";
		String name2 = "The Big Apple";
		etype = null;

		double similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertEquals(1.0, similarity);
	}

	@Test
	public final void testDictionaryLookup5() {
		setUpDictionaryLookup();

		String name1 = "AAAAAAAAAAAAA";
		String name2 = "BBBBBBB";

		double similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertEquals(0.0, similarity);
	}

	@Test
	public final void testDictionaryLookup6() {
		setUpDictionaryLookup();

		String name1 = "New York";
		String name2 = "New York State";

		double similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertEquals(0.0, similarity);
	}

	@Test
	public final void testDictionaryLookup7() {
		setUpDictionaryLookup();

		String name1 = "New York";
		String name2 = "The Big Appla";
		etype = null;

		double similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertTrue(similarity > 0.0);
	}

	@Test
	public final void testDictionaryLookup8() {
		setUpDictionaryLookup();

		String name1 = "New York";
		String name2 = "The Big Application";

		double similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertEquals(0.0, similarity);
	}

	@Test
	public final void testDictionaryLookup9() {
		setUpDictionaryLookup();

		String name1 = "New Yorker";
		String name2 = "The Big Apple";

		double similarity = nameMatch.dictionaryLookup(name1, name2, etype);
		assertEquals(0.0, similarity);
	}



}
