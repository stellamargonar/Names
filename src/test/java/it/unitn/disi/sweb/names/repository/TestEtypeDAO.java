package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.utils.bootstrap.DatabaseBootstrap;

import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@Transactional(rollbackFor = Throwable.class)
public class TestEtypeDAO extends TestCase {

	@Autowired
	ETypeDAO dao;
	@Autowired
	DatabaseBootstrap db;

	private EType e;
	private int id;
	private String name = "Test";

	@BeforeTransaction
	public void verifyInitialDatabaseState() {
		// TODO empty db
	}

	@AfterTransaction
	public void verifyFinalDatabaseState() {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		e = new EType();
		e.setEtype(name);
		id = dao.save(e).getId();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		dao.delete(e);
	}

	@Test
	public final void testSave() {
		EType n = new EType();
		n.setEtype("NEW");

		EType returned = dao.save(n);

		assertNotNull(returned);
		assertEquals(n.getEtype(), returned.getEtype());
	}

	@Test
	public final void testUpdate() {
		name = "TESTED";
		e.setEtype(name);
		EType returned = dao.update(e);

		assertNotNull(returned);
		assertEquals(e.getEtype(), returned.getEtype());

		e = returned;
	}

	@Test
	public final void testFindById() {
		EType found = dao.findById(id);

		assertNotNull(found);
		assertEquals(e.getEtype(), found.getEtype());
	}

	@Test
	public final void testFindByName() {
		EType found = dao.findByName(name);

		assertNotNull(found);
		assertEquals(e.getEtype(), found.getEtype());
	}

	@Test
	public final void testFindAll() {
		List<EType> found = dao.findAll();

		assertNotNull(found);
		assertTrue(found.size() > 0);
	}

	// @Test
	public final void testDelete() {
		dao.delete(e);
		assertNull(dao.findById(e.getId()));
	}

//	@Test
	@Rollback(true)
	public final void testDeleteAll() {
		dao.deleteAll();
		Assert.assertTrue(dao.findAll() == null || dao.findAll().isEmpty());
	}

}
