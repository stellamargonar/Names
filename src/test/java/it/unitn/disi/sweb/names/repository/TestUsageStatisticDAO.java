package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameManager;

import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@Transactional(rollbackFor = Throwable.class)
public class TestUsageStatisticDAO extends TestCase {

	@Autowired
	UsageStatisticsDAO dao;
	@Autowired
	NameManager nameManager;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	EntityManager entityManager;

	private UsageStatistic stat;
	private String query = "Test";
	private FullName selected;

	@Override
	@Before
	public void setUp() throws Exception {
		NamedEntity entity = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.PERSON), "test");
		selected = nameManager.createFullName("Test", entity);

		stat = new UsageStatistic();
		stat.setQuery(query);
		stat.setSelected(selected);
		stat.setFrequency(10);

		stat = dao.save(stat);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(true)
	public final void testSave() {
		UsageStatistic s = new UsageStatistic();
		s.setSelected(selected);
		s.setQuery("testsave");
		s.setFrequency(1);

		checkEquals(s, dao.save(s));

	}

	@Test
	public final void testUpdate() {
		stat.setFrequency(1000);
		checkEquals(stat, dao.update(stat));

		stat.setQuery("testupdate");
		checkEquals(stat, dao.update(stat));
	}

	// @Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindById() {
		checkEquals(stat, dao.findById(stat.getId()));
	}

	@Test
	public final void testFindByQuery() {
		List<UsageStatistic> list = dao.findByQuery(query);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (UsageStatistic s : list) {
			assertNotNull(s);
			assertEquals(query, s.getQuery());
		}
	}

	@Test
	public final void testFindByQuerySelected() {
		checkEquals(stat, dao.findByQuerySelected(query, selected));
		assertNull(dao.findByQuerySelected("", selected));
	}

	@Test
	public final void testFindAll() {
		List<UsageStatistic> list = dao.findAll();
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}

	private void checkEquals(UsageStatistic source, UsageStatistic target) {
		assertNotNull(target);
		assertEquals(source.getFrequency(), target.getFrequency());
		assertEquals(source.getQuery(), target.getQuery());
		assertEquals(source.getSelected(), target.getSelected());
	}

}
