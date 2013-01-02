/**
 *
 */
package it.unitn.disi.sweb.names.service;

import static org.junit.Assert.fail;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.utils.Pair;

import java.util.ArrayList;
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
public class TestAutocompletion {

	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;

	@Autowired
	PrefixManager prefixManager;

	@Autowired
	StatisticsManager statManager;

	@Autowired
	NameAutocompletion autocompletion;

	private Integer[][] guids;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		createEntities();
		setUpPrefixes();
	}

	private void createEntities() {
		this.guids = new Integer[5][];

		String[] names1 = {"Gardolo", "Gardolo Basket"};
		String url1 = "www.basketgardolo.it";

		String[] names2 = {"Lago di Garda", "Garda"};
		String url2 = "http://it.wikipedia.org/wiki/Lago_di_Garda";

		String[] names3 = {"Garda Lake"};
		String url3 = "http://en.wikipedia.org/wiki/Lake_Garda";
		EtypeName type = EtypeName.LOCATION;

		this.guids[0] = createEntity(names1, type, url1);
		this.guids[1] = createEntity(names2, type, url2);
		this.guids[2] = createEntity(names3, type, url3);

		String[] names4 = {"Fausto Giunchiglia", "Professor Giunchiglia",
				"Supervisor"};
		String url4 = "http://disi.unitn.it/~fausto/";

		String[] names5 = {"Imperatore Cesare", "Giulio Cesare"};
		String url5 = "http://disi.unitn.it/~fausto/";

		this.guids[3] = createEntity(names4, EtypeName.PERSON, url4);
		this.guids[4] = createEntity(names5, EtypeName.PERSON, url5);
	}

	private void setUpPrefixes() {
		FullName gardolo = this.nameManager.find(this.guids[0][0]);
		System.out.println(gardolo);
		// update (fake) statistics
		this.statManager.updateSearchStatistic("gardolo", gardolo);
		this.statManager.updateSearchStatistic("gardolo", gardolo);
		this.statManager.updateSearchStatistic("gardol", gardolo);

		FullName garda = this.nameManager.find(this.guids[1][0]);
		this.statManager.updateSearchStatistic("garda", garda);
		this.statManager.updateSearchStatistic("lago di Garda", garda);

		FullName lake = this.nameManager.find(this.guids[2][0]);
		this.statManager.updateSearchStatistic("lago di Garda", lake);

		FullName giunchiglia = this.nameManager.find(this.guids[3][0]);
		this.statManager.updateSearchStatistic("fausto", giunchiglia);
		this.statManager.updateSearchStatistic("fasuto", giunchiglia);
		this.statManager.updateSearchStatistic("Giunchiglia", giunchiglia);
		this.statManager.updateSearchStatistic("Giunchiglia", giunchiglia);

		FullName cesare = this.nameManager.find(this.guids[4][1]);
		this.statManager.updateSearchStatistic("cesare", cesare);
		this.statManager.updateSearchStatistic("caesar", cesare);
		this.statManager.updateSearchStatistic("Giulio Cesare", cesare);

		this.prefixManager.updatePrefixes();

	}
	private Integer[] createEntity(String[] names, EtypeName etype, String url) {
		NamedEntity ne = null;
		List<Integer> ids = new ArrayList<>();

		List<NamedEntity> list = this.entityManager.find(url, names[0]);
		if (list != null && !list.isEmpty()) {
			ne = list.get(0);
		} else {
			ne = this.entityManager.createEntity(
					this.etypeManager.getEtype(etype), url);
		}

		for (String name : names) {
			if (name != null) {
				ids.add(this.nameManager.createFullName(name, ne).getId());
			}
		}
		return ids.toArray(new Integer[names.length]);
	}
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link it.unitn.disi.sweb.names.service.NameAutocompletion#searchNamePrefix(java.lang.String)}
	 * .
	 */
	@Test
	public final void testSearchNamePrefixString() {
		String prefix = "giu";
		List<Pair<FullName, Double>> result = this.autocompletion
				.searchNamePrefix(prefix);
		printResult(prefix, result);
	}
	private void printResult(String query, List<Pair<FullName, Double>> result) {
		Logger logger = Logger.getAnonymousLogger();
		logger.log(Level.INFO, "QUERY: " + query + ", ngram: "
				+ this.nameManager.computeNGram(query));
		for (Pair<FullName, Double> p : result) {
			logger.log(Level.INFO, "\t" + p.key.getName() + "\t\t" + p.value);
		}
	}

	/**
	 * Test method for
	 * {@link it.unitn.disi.sweb.names.service.NameAutocompletion#searchNamePrefix(java.lang.String, it.unitn.disi.sweb.names.model.EType)}
	 * .
	 */
	@Test
	public final void testSearchNamePrefixStringEType() {
		fail("Not yet implemented"); // TODO
	}

}
