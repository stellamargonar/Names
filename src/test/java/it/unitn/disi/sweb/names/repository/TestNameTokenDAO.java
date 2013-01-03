package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.NamedEntity;
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
public class TestNameTokenDAO extends TestCase {

	@Autowired
	NameTokenDAO dao;

	private NameToken nt;
	private FullName fullName;
	private IndividualName individualName;
	private int id;

	@Autowired
	FullNameDAO fullDao;
	@Autowired
	IndividualNameDAO individualDao;
	@Autowired
	EntityDAO entityDao;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	ElementManager elementManager;


	@Override
	@Before
	public void setUp() throws Exception {
		NamedEntity entity = new NamedEntity();
		entity.setEType(etypeManager.getEtype(EtypeName.PERSON));
		entityDao.save(entity);

		fullName = new FullName();
		fullName.setName("test testing");
		fullName.setEntity(entity);
		fullName = fullDao.save(fullName);

		individualName = new IndividualName();
		individualName.setName("test");
		individualName.setNameElement(elementManager.findNameElement("GivenName", etypeManager.getEtype(EtypeName.PERSON)));
		individualName = individualDao.save(individualName);

		nt = new NameToken();
		nt.setFullName(fullName);
		nt.setIndividualName(individualName);
		nt.setPosition(1);
		nt = dao.save(nt);
		id = nt.getId();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		dao.delete(nt);
	}

	@Test
	@Rollback(true)
	public final void testSave() {
		NameToken n = new NameToken();
		n.setFullName(fullName);
		n.setIndividualName(individualName);
		n.setPosition(3);
		checkEquals(n, dao.save(n));
	}

	@Test
	public final void testUpdate() {
		nt.setPosition(2);

		checkEquals(nt, dao.update(nt));
	}

	// @Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindById() {
		checkEquals(nt, dao.findById(id));
	}

	@Test
	public final void testFindByFullNameIndividualName() {
		checkEquals(nt,
				dao.findByFullNameIndividualName(fullName, individualName));
	}

	@Test
	public final void testFindByFullName() {
		List<NameToken> tokens = dao.findByFullName(fullName);
		for (NameToken n : tokens) {
			assertNotNull(n);
			assertEquals(n.getFullName(), nt.getFullName());
		}
	}

	@Test
	public final void testFindByIndividualName() {
		List<NameToken> tokens = dao.findByIndividualName(individualName);
		for (NameToken n : tokens) {
			assertNotNull(n);
			assertEquals(n.getIndividualName(), nt.getIndividualName());
		}
	}

	private void checkEquals(NameToken source, NameToken target) {
		assertNotNull(target);
		assertEquals(target.getFullName(), source.getFullName());
		assertEquals(target.getIndividualName(), source.getIndividualName());
		assertEquals(target.getPosition(), source.getPosition());
	}
}
