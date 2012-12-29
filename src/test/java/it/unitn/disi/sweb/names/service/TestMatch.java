/**
 *
 */
package it.unitn.disi.sweb.names.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author stella
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class TestMatch {

	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;

	@Autowired
	NameMatch nameMatch;

	/**
	 * sets up data for testing
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		String[] names1 = {"Garda", "Citta' Garda"};
		String url1 = "http://www.comunedigarda.it";

		String[] names2 = {"Lago di Garda", "Garda"};
		String url2 = "http://it.wikipedia.org/wiki/Lago_di_Garda";

		String[] names3 = {"Garda Lake"};
		String url3 = "http://en.wikipedia.org/wiki/Lake_Garda";
		EtypeName type = EtypeName.LOCATION;

		createEntity(names1, type, url1);
		createEntity(names2, type, url2);
		createEntity(names3, type, url3);

		String[] names4 = {"Fausto Giunchiglia", "Professor Giunchiglia",
				"Supervisor"};
		String url4 = "http://disi.unitn.it/~fausto/";

		createEntity(names4, EtypeName.PERSON, url4);

	}

	private void createEntity(String[] names, EtypeName etype, String url) {
		NamedEntity ne = null;
		List<NamedEntity> list = this.entityManager.find(url, names[0]);
		if (list != null && !list.isEmpty()) {
			ne = list.get(0);
		} else {
			ne = this.entityManager.createEntity(
					this.etypeManager.getEtype(etype), url);
		}

		for (String name : names) {
			if (name != null) {
				this.nameManager.createFullName(name, ne);
			}
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
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
	public final void testStringSimilarityString() {
		EType etype = this.etypeManager.getEtype(EtypeName.PERSON);
		String name1 = "Stella Margonar";
		String name2 = "Stella Margonar";

		double similarity = this.nameMatch
				.stringSimilarity(name1, name2, etype);
		printResult(name1, name2, similarity);
		assertEquals(similarity, 1, 0.1);

		name1 = "Stella Margonar";
		name2 = "stelaa Margar";

		similarity = this.nameMatch.stringSimilarity(name1, name2, etype);

		printResult(name1, name2, similarity);
		assertTrue(similarity > 0.5);
	}

	/**
	 * Test method for
	 * {@link it.unitn.disi.sweb.names.service.NameMatch#stringSimilarity(it.unitn.disi.sweb.names.model.FullName, it.unitn.disi.sweb.names.model.FullName, it.unitn.disi.sweb.names.model.EType)}
	 * .
	 */
	@Test
	public final void testStringSimilarityFullName() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link it.unitn.disi.sweb.names.service.NameMatch#dictionaryLookup(java.lang.String, java.lang.String, it.unitn.disi.sweb.names.model.EType)}
	 * .
	 */
	@Test
	public final void testDictionaryLookup() {
		String name1 = "Fausto Giunchiglia";
		String name2 = "Supervisor";
		EType etype = this.etypeManager.getEtype(EtypeName.PERSON);

		double similarity = this.nameMatch
				.dictionaryLookup(name1, name2, etype);

		printResult(name1, name2, similarity);
		assertTrue(similarity > 0.8);
	}

	/**
	 * Test method for
	 * {@link it.unitn.disi.sweb.names.service.NameMatch#tokenAnalysis(java.lang.String, java.lang.String, it.unitn.disi.sweb.names.model.EType)}
	 * .
	 */
	@Test
	public final void testTokenAnalysis() {
		EType etype = this.etypeManager.getEtype(EtypeName.LOCATION);

		String name1 = "Lago di Garda";
		String name2 = "Garda Loke";

		double similarity = this.nameMatch.tokenAnalysis(name1, name2, etype);
		printResult(name1, name2, similarity);
		assertTrue(similarity >= 0.5);
	}

	private void printResult(String name1, String name2, double result) {
		Logger.getAnonymousLogger().log(Level.INFO,
				"f(" + name1 + ", " + name2 + ") = " + result);
	}

}
