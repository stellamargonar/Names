package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordStatistic;
import it.unitn.disi.sweb.names.service.ElementManager;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;

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
public class TestTriggerWordStatisticDAO extends TestCase {

	@Autowired
	TriggerWordStatisticDAO dao;
	@Autowired
	TriggerWordDAO twDao;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	ElementManager elManager;

	private TriggerWordStatistic stat;
	private TriggerWord t;
	private EType etype;
	@Override
	@Before
	public void setUp() throws Exception {
		etype = etypeManager.getEtype(EtypeName.ORGANIZATION);
		t = new TriggerWord("Test", elManager.findTriggerWordType("Toponym",
				etypeManager.getEtype(EtypeName.LOCATION)));
		t = twDao.save(t);

		stat = new TriggerWordStatistic();
		stat.seteType(etype);
		stat.setTriggerWord(t);
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
		TriggerWordStatistic s = new TriggerWordStatistic();
		s.seteType(etypeManager.getEtype(EtypeName.LOCATION));
		s.setTriggerWord(t);

		checkEquals(s, dao.save(s));
	}

	@Test
	public final void testUpdate() {
		stat.seteType(etypeManager.getEtype(EtypeName.PERSON));
		checkEquals(stat, dao.update(stat));

		TriggerWord u = new TriggerWord("TestUpdate",
				elManager.findTriggerWordType("Qualifier",
						etypeManager.getEtype(EtypeName.LOCATION)));
		u = twDao.save(u);
		stat.setTriggerWord(u);
		checkEquals(stat, dao.update(stat));

		stat.setFrequency(30);
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
	public final void testFindByTriggerWordEtype() {
		checkEquals(stat, dao.findByTriggerWordEtype(t, etype));
	}

	@Test
	public final void testFindByTriggerWord() {
		List<TriggerWordStatistic> list = dao.findByTriggerWord(t);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (TriggerWordStatistic s : list) {
			assertNotNull(s);
			assertEquals(s.getTriggerWord(), t);
		}
	}

	private void checkEquals(TriggerWordStatistic source,
			TriggerWordStatistic target) {
		assertNotNull(target);
		assertEquals(source.getTriggerWord(), target.getTriggerWord());
		assertEquals(source.geteType(), target.geteType());
		assertEquals(source.getFrequency(), target.getFrequency());

	}

}
