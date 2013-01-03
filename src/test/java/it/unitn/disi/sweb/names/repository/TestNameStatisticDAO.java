package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameStatistics;
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
public class TestNameStatisticDAO extends TestCase {

	@Autowired
	NameStatisticDAO dao;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	ElementManager elManager;
	@Autowired
	IndividualNameDAO nameDAO;

	private NameStatistics stat;
	private NameElement el;
	private IndividualName name;
	private int id;

	@Override
	@Before
	public void setUp() throws Exception {
		name = new IndividualName();
		name.setName("testname");
		name.setNameElement(elManager.findNameElement("ProperNoun",
				etypeManager.getEtype(EtypeName.LOCATION)));
		name = nameDAO.save(name);

		el = elManager.findNameElement("GivenName",
				etypeManager.getEtype(EtypeName.PERSON));

		stat = new NameStatistics();
		stat.setName(name);
		stat.setFrequency(100);
		stat.setNameElement(el);

		stat = dao.save(stat);
		id = stat.getId();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		dao.delete(stat);
	}

	@Test
	@Rollback(true)
	public final void testSave() {
		NameStatistics s = new NameStatistics();
		s.setName(name);
		s.setNameElement(elManager.findNameElement("ProperNoun",
				etypeManager.getEtype(EtypeName.LOCATION)));
		s.setFrequency(10);

		NameStatistics returned = dao.save(s);
		checkEquals(s, returned);
	}

	@Test
	public final void testUpdate() {
		stat.setFrequency(4);
		checkEquals(stat, dao.update(stat));

		stat.setNameElement(elManager.findNameElement("FamilyName",
				etypeManager.getEtype(EtypeName.PERSON)));
		checkEquals(stat, dao.update(stat));
	}
//	@Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindById() {
		checkEquals(stat, dao.findById(id));
	}

	@Test
	public final void testFindByNameElement() {
		checkEquals(
				stat,
				dao.findByNameElement(
						name,
						elManager.findNameElement("GivenName",
								etypeManager.getEtype(EtypeName.PERSON))));

	}
	@Test
	public final void testFindByName() {
		List<NameStatistics> found = dao.findByName(name);
		assertNotNull(found);
		assertTrue(found.size() > 0);

		for (NameStatistics n : found) {
			assertNotNull(n);
			assertEquals(n.getName(), stat.getName());
		}
	}

	private void checkEquals(NameStatistics source, NameStatistics target) {
		assertNotNull(target);
		assertEquals(target.getName(), source.getName());
		assertEquals(target.getNameElement(), source.getNameElement());
		assertEquals(target.getFrequency(), source.getFrequency());
	}

}
