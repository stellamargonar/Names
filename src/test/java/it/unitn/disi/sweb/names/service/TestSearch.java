package it.unitn.disi.sweb.names.service;

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
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@Transactional(rollbackFor = Throwable.class)
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

	}


	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testNameSearch() {
		String query = "Garda Loke";
		List<Pair<String, Double>> result = nameSearch.nameSearch(query);
		printResult(query, result);

	}

	private void printResult(String query, List<Pair<String, Double>> result) {
		Logger logger = Logger.getAnonymousLogger();
		logger.log(Level.INFO, "QUERY: " + query + ", ngram: "
				+ nameManager.computeNGram(query));
		for (Pair<String, Double> p : result) {
			logger.log(Level.INFO, "\t" + p.key + "\t\t" + p.value);
		}
	}

}
