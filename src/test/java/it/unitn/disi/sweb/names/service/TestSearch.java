package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.repository.UsageStatisticsDAO;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
public class TestSearch extends TestCase {
	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	StatisticsManager statManager;
	@Autowired
	UsageStatisticsDAO dao;

	@Autowired
	NameSearch nameSearch;

	private NamedEntity e1;
	private NamedEntity e2;
	private NamedEntity e3;
	private NamedEntity p1;
	private NamedEntity roma;
	private NamedEntity romano;
	private NamedEntity romaCalcio;

	@Override
	@Before
	public void setUp() throws Exception {
		Logger.getLogger("org.hibernate.SQL").setLevel(Level.OFF);

		p1 = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.PERSON),
				"http://disi.unitn.it/~fausto/");
		FullName f1 = nameManager.createFullName("Prof Giunchiglia", p1);
		FullName f2 = nameManager.createFullName("Fausto Giunchiglia", p1);

		e1 = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION),
				"http://it.wikipedia.org/wiki/Piazza_San_Pietro");
		nameManager.createFullName("Piazza San Pietro", e1);
		nameManager.createFullName("Saint Peter Square", e1);

		e2 = entityManager
				.createEntity(etypeManager.getEtype(EtypeName.LOCATION),
						"http://it.wikipedia.org/wiki/Basilica_di_San_Pietro_in_Vincoli");
		nameManager.createFullName("San Pietro in Vincoli", e2);

		e3 = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION),
				"http://it.wikipedia.org/wiki/Roma");
		nameManager.createFullName("Roma", e3);

		roma = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION),
				"http://it.wikipedia.org/wiki/Roma");
		FullName roma1 = nameManager.createFullName("Roma", roma);
		FullName roma2 = nameManager.createFullName("Rome", roma);

		romano = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION),
				"http://www.comune.romano.vi.it");
		FullName romano1 = nameManager.createFullName("Romano (VI)", romano);
		FullName romano2 = nameManager.createFullName("Romano d'Ezzelino",
				romano);

		romaCalcio = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.ORGANIZATION),
				"http://www.asroma.it/it/index.html");
		FullName calcio1 = nameManager.createFullName("Roma", romaCalcio);
		FullName calcio2 = nameManager.createFullName("AS Roma", romaCalcio);
		FullName calcio3 = nameManager
				.createFullName("Roma Calcio", romaCalcio);

		statManager.updateSearchStatistic("roma", roma1);
		statManager.updateSearchStatistic("roma", roma1);
		statManager.updateSearchStatistic("roma", roma1);
		statManager.updateSearchStatistic("roma", roma1);

		statManager.updateSearchStatistic("roma", calcio1);

		statManager.updateSearchStatistic("roman", roma1);
		statManager.updateSearchStatistic("roman", roma1);

		statManager.updateSearchStatistic("romano", romano1);
		statManager.updateSearchStatistic("romano", romano1);

		statManager.updateSearchStatistic("roman", romano1);

		UsageStatistic u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(roma1);
		u.setFrequency(100);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(roma2);
		u.setFrequency(90);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(calcio1);
		u.setFrequency(80);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(calcio2);
		u.setFrequency(70);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(calcio3);
		u.setFrequency(50);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(romano2);
		u.setFrequency(30);
		dao.save(u);


		Logger.getLogger("org.hibernate.SQL").setLevel(Level.DEBUG);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/* test all the other test cases */
	@Test
	public void testSearch1() {
		assertTrue(nameSearch.nameSearch(null).isEmpty());
	}

	@Test
	public void testSearch2() {
		assertTrue(nameSearch.nameSearch("test").isEmpty());
	}

	@Test
	public void testSearch3() {
		List<Map.Entry<String, Double>> result = nameSearch.nameSearch("roman");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Roma", result));
		assertTrue(contains("Roma Calcio", result));
		assertTrue(contains("Romano d'Ezzelino", result));
	}

	@Test
	public void testSearch4() {
		List<Map.Entry<String, Double>> result = nameSearch.nameSearch("roma");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Rome", result));
		assertTrue(contains("Roma Calcio", result));
		assertTrue(contains("Romano d'Ezzelino", result));
	}

	@Test
	public void testSearch5() {
		List<Map.Entry<String, Double>> result = nameSearch.nameSearch("rom");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Roma", result));
		assertTrue(contains("Roma Calcio", result));
	}

	@Test
	public void testSearch6() {
		List<Map.Entry<String, Double>> result = nameSearch.nameSearch("aroma");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Roma", result));
		assertTrue(contains("Roma Calcio", result));
	}

	@Test
	public void testSearch7() {
		List<Map.Entry<String, Double>> result =nameSearch.nameSearch("");
		assertNotNull(result);
		for (Map.Entry<String,Double>p: result) {
			System.out.println(p.getKey());
		}
		assertTrue(result.isEmpty());

	}

	@Test
	public void testSearch8() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Giunchiglia");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Prof Giunchiglia", result));
		assertTrue(contains("Fausto Giunchiglia", result));
	}

	@Test
	public void testSearch9() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Prof Giunchiglia");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Prof Giunchiglia", result));
		assertTrue(contains("Fausto Giunchiglia", result));
	}

	@Test
	public void testSearch10() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Giunchiglia Fausto");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Prof Giunchiglia", result));
		assertTrue(contains("Fausto Giunchiglia", result));
	}

	@Test
	public void testSearch11() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Roma As");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Roma Calcio", result));
		assertTrue(contains("AS Roma", result));
		assertTrue(contains("Roma", result));
	}

	@Test
	public void testSearch12() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("San Pietro piazza");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Piazza San Pietro", result));
		assertTrue(contains("Saint Peter Square", result));
	}

	@Test
	public void testSearch13() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Fasuto Giunchiglia");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Prof Giunchiglia", result));
		assertTrue(contains("Fausto Giunchiglia", result));
	}

	@Test
	public void testSearch14() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Santo Pietro Piaza");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Piazza San Pietro", result));
		assertTrue(contains("Saint Peter Square", result));
	}

	@Test
	public void testSearch15() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Ramano d0eZelino");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Romano (VI)", result));
		assertTrue(contains("Romano d'Ezzelino", result));
	}

	@Test
	public void testSearch16() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Santo Pietro");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Piazza San Pietro", result));
		assertTrue(contains("Saint Peter Square", result));
		assertTrue(contains("San Pietro in Vincoli", result));
	}


	@Test
	public void testSearch17() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Piazza");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Piazza San Pietro", result));
		assertTrue(contains("Saint Peter Square", result));
	}

	@Test
	public void testSearch18() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("Piaza");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Piazza San Pietro", result));
		assertTrue(contains("Saint Peter Square", result));
	}

	@Test
	public void testSearch19() {
		List<Map.Entry<String, Double>> result = nameSearch
				.nameSearch("santo petro");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(contains("Piazza San Pietro", result));
		assertTrue(contains("Saint Peter Square", result));
		assertTrue(contains("San Pietro in Vincoli", result));
	}

	private boolean contains(String s, List<Map.Entry<String, Double>> result) {
		boolean found = false;
		for (Map.Entry<String, Double> p : result) {
			if (p.getKey().equals(s)) {
				found = true;
			}
		}
		return found;
	}

}
