package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.utils.Pair;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class TestSearch {

	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;

	@Autowired
	NameSearch nameSearch;

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

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testNameSearch() {
		String query = "Garda Loke";
		List<Pair<String, Double>> result = this.nameSearch.nameSearch(query);
		printResult(query, result);

	}

	private void printResult(String query, List<Pair<String, Double>> result) {
		Logger logger = Logger.getAnonymousLogger();
		logger.log(Level.INFO, "QUERY: " + query + ", ngram: "
				+ this.nameManager.computeNGram(query));
		for (Pair<String, Double> p : result) {
			logger.log(Level.INFO, "\t" + p.key + "\t\t" + p.value);
		}
	}

}
