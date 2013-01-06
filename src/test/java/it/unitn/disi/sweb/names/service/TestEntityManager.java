package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.EntityDAO;

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
public class TestEntityManager extends TestCase {

	@Autowired
	EntityManager manager;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	EntityDAO dao;
	@Autowired
	NameManager nameManager;

	private NamedEntity entity;
	private EType etype;
	private String url = "test";

	@Override
	@Before
	public void setUp() throws Exception {
		etype = etypeManager.getEtype(EtypeName.PERSON);
		entity = new NamedEntity();
		entity.setEType(etype);
		entity.setUrl(url);
		entity = dao.save(entity);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(true)
	public final void testCreateEntity() {
		NamedEntity e = manager.createEntity(etype, url);
		assertNotNull(e);
		assertEquals(etype, entity.getEType());
		assertEquals(url, e.getUrl());

		e = manager.createEntity(null, null);
		assertNull(e);
	}

	@Test
	public final void testFindInt() {
		NamedEntity e = manager.find(entity.getGUID());
		assertNotNull(e);
		assertEquals(etype, entity.getEType());
		assertEquals(url, e.getUrl());
	}

	@Test
	public final void testFindStringString() {
		String name = "TEST";
		nameManager.createFullName(name, entity);
		List<NamedEntity> list = manager.find(url, name);

		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (NamedEntity e : list) {
			assertNotNull(e);
			assertEquals(url, e.getUrl());
		}

		list = manager.find((String) null, (String) null);
		assertNotNull(list);
		assertTrue(list.size() == 0);

		list = manager.find(url, (String) null);
		assertNotNull(list);
		assertTrue(list.size() > 0);

		list = manager.find(null, name);
		assertNotNull(list);
		assertTrue(list.size() > 0);

		// unexisting name
		list = manager.find((String) null, "zzzzzzz");
		assertNotNull(list);
		assertTrue(list.size() == 0);

		// unexisting name
		list = manager.find("zzzzzzz", (String) null);
		assertNotNull(list);
		assertTrue(list.size() == 0);

	}

	@Test
	public final void testFindString() {
		String name = "TEST";
		nameManager.createFullName(name, entity);
		List<NamedEntity> list = manager.find(name);

		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (NamedEntity e : list) {
			assertNotNull(e);
		}

	}

	@Test
	public final void testFindStringEType() {
		String name = "TEST";
		nameManager.createFullName(name, entity);
		List<NamedEntity> list = manager.find(name, etype);

		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (NamedEntity e : list) {
			assertNotNull(e);
			assertEquals(etype, e.getEType());
		}
	}

	@Test
	public final void testFindEType() {
		List<NamedEntity> list = manager.find(etype);

		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (NamedEntity e : list) {
			assertNotNull(e);
			assertEquals(etype, e.getEType());
		}
	}

}
