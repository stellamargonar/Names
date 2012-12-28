package it.unitn.disi.sweb.names.utils;

import java.util.List;

import it.unitn.disi.sweb.names.service.NameManager;
import it.unitn.disi.sweb.names.service.NameSearch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class TestNameSearch {

	@Autowired
	NameSearch nameSearch;
	@Autowired
	NameManager nameManager;

	@Test
	public void searchEquals() {
		String query = "Garda Loke";
		List<Pair<String, Double>> result = nameSearch.nameSearch(query);
		printResult(query, result);
	}

	private void printResult(String query, List<Pair<String, Double>> result) {
		System.out.println("QUERY: " + query + ", " + nameManager.computeNGram(query));
		for (Pair p : result)
			System.out.println("\t" + p.key + "\t\t" + p.value);
	}
}
