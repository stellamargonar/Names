package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
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
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@Transactional(rollbackFor = Throwable.class)
public class TestEntityDAO extends TestCase {

	@Autowired
	EntityDAO dao;

	@Autowired
	EtypeManager etypeManager;

	@Autowired
	NameManager nameManager;

	private NamedEntity e;
	private int id;
	private String url = "url";
	private EType etype;
	private String name = "nametest";

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
		e = new NamedEntity();

		etype = etypeManager.getEtype(EtypeName.PERSON);
		e.setEType(etype);

		e.setUrl(url);
		id = dao.save(e).getGUID();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		dao.delete(e);
	}

	@Test
	@Rollback(true)
	public final void testSave() {
		NamedEntity n = new NamedEntity();
		n.setEType(etype);
		n.setUrl("TEST");

		NamedEntity returned = dao.save(n);

		assertNotNull(returned);
		assertEquals(n.getUrl(), returned.getUrl());
		assertEquals(n.getEType(), returned.getEType());
	}

	@Test
	public final void testUpdate() {
		url = "TESTED";
		e.setUrl(url);
		NamedEntity returned = dao.update(e);

		assertNotNull(returned);
		assertEquals(e.getUrl(), returned.getUrl());
		assertEquals(e.getEType(), returned.getEType());

		e = returned;
	}

//	@Test
	public final void testDelete() {
		dao.delete(e);
		System.out.println(id + ": " + dao.findById(id));
		assertNull(dao.findById(id));
	}

	@Test
	public final void testFindById() {
		NamedEntity found = dao.findById(id);

		assertNotNull(found);
		assertEquals(e.getUrl(), found.getUrl());
		assertEquals(e.getEType(), found.getEType());
	}

	@Test
	@Rollback(true)
	public final void testFindByName() {
		createName();
		List<NamedEntity> found = dao.findByName(name);

		assertNotNull(found);
		assertTrue(found.size() > 0);
	}

	@Test
	@Rollback(true)
	public final void testFindByNameEtype() {
		createName();
		List<NamedEntity> found = dao.findByNameEtype(name, etype);

		assertNotNull(found);
		assertTrue(found.size() > 0);
		for (NamedEntity entity : found) {
			assertEquals(entity.getEType(), etype);
		}
	}

	@Test
	public final void testFindByUrl() {
		List<NamedEntity> found = dao.findByUrl(url);

		assertNotNull(found);
		assertTrue(found.size() > 0);
		for (NamedEntity entity : found) {
			assertEquals(entity.getUrl(), url);
		}
	}

	@Test
	@Rollback(true)
	public final void testFindByNameUrl() {
		createName();
		List<NamedEntity> found = dao.findByNameUrl(name, url);

		assertNotNull(found);
		assertTrue(found.size() > 0);
		for (NamedEntity entity : found) {
			assertEquals(entity.getUrl(), url);
		}
	}

	@Test
	public final void testFindByEtype() {
		List<NamedEntity> found = dao.findByEtype(etype);

		assertNotNull(found);
		assertTrue(found.size() > 0);
		for (NamedEntity entity : found) {
			assertEquals(entity.getEType(), etype);
		}
	}

	private void createName() {
		nameManager.createFullName(name, e);
	}
}
