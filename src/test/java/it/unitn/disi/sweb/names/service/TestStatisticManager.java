package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.repository.UsageStatisticsDAO;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class TestStatisticManager extends TestCase {

	@Autowired
	StatisticsManager manager;
	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	UsageStatisticsDAO dao;

	private FullName fullName;
	private String query = "query";
	@Override
	@Before
	public void setUp() throws Exception {
		fullName = nameManager.createFullName(
				"Test",
				entityManager.createEntity(
						etypeManager.getEtype(EtypeName.PERSON), "test"));
		UsageStatistic u = new UsageStatistic();
		u.setQuery(query);
		u.setSelected(fullName);
		u.setFrequency(1);
		dao.save(u);

	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testUpdateSearchStatistic() {
		String query1 = "TEST";
		double oldFrequency = manager.retrieveFrequency(query1, fullName);
		manager.updateSearchStatistic(query1, fullName);
		double newFrequency = manager.retrieveFrequency(query1, fullName);

		assertEquals(oldFrequency + 1, newFrequency);

		oldFrequency = newFrequency;
		manager.updateSearchStatistic(query1, fullName);
		newFrequency = manager.retrieveFrequency(query1, fullName);

		assertEquals(oldFrequency + 1, newFrequency);

		oldFrequency = manager.retrieveFrequency(query, fullName);
		manager.updateSearchStatistic(query, fullName);
		newFrequency = manager.retrieveFrequency(query, fullName);

		assertEquals(oldFrequency + 1, newFrequency);
	}

	@Test
	public final void testUpdateMatchStatistic() {
		// updateMatchStatistic(String source, String target, double
		// similarity);
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRetrieveTopResults() {
		List<UsageStatistic> list = dao.findByQuery(query);
		int maxResult = 10;

		Map<FullName, Double> top = manager
				.retrieveTopResults(query, maxResult);

		Map<FullName, Double> topList = new HashMap<>(maxResult);
		Collections.sort(list, new Comparator<UsageStatistic>() {

			@Override
			public int compare(UsageStatistic o1, UsageStatistic o2) {
				return Double.compare(o1.getFrequency(), o2.getFrequency());
			}
		});

		for (int i = 0; i < list.size() && i < maxResult; i++) {
			topList.put(list.get(i).getSelected(), list.get(i).getFrequency());
		}

		for (FullName f : top.keySet()) {
			assertEquals(topList.get(f), top.get(f));
		}
	}
}
