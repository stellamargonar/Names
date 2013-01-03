package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
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
public class TestNameElementDAO extends TestCase {

	@Autowired
	NameElementDAO dao;

	@Autowired
	EtypeManager etypeManager;

	private NameElement el;
	private String name = "test";
	private int id;
	private EType etype;
	@Override
	@Before
	public void setUp() throws Exception {
		el = new NameElement();
		el.setElementName(name);

		etype = etypeManager.getEtype(EtypeName.PERSON);
		el.setEtype(etype);
		id = dao.save(el).getId();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		dao.delete(el);
	}

	@Test
	@Rollback(true)
	public final void testSave() {
		el = new NameElement();
		NameElement n = new NameElement();
		n.setElementName(name + "save");

		n.setEtype(etypeManager.getEtype(EtypeName.PERSON));
		NameElement returned = dao.save(n);

		assertNotNull(returned);
		assertEquals(returned.getElementName(), n.getElementName());
		assertEquals(returned.getEtype(), n.getEtype());
	}

	@Test
	public final void testUpdate() {
		el.setElementName(name + "update");
		NameElement returned = dao.update(el);
		assertNotNull(returned);
		assertEquals(returned.getElementName(), el.getElementName());
		assertEquals(returned.getEtype(), el.getEtype());
		el = returned;
	}

//	@Test
	public final void testDelete() {
		dao.delete(el);
		assertNull(dao.findById(id));
	}

	@Test
	public final void testFindById() {
		NameElement found = dao.findById(id);
		assertNotNull(found);
		assertEquals(found.getElementName(), el.getElementName());
		assertEquals(found.getEtype(), el.getEtype());
	}

	@Test
	public final void testFindName() {
		List<NameElement> found = dao.findName(name);
		assertNotNull(found);
		assertTrue(found.size() > 0);
		for (NameElement ne : found) {
			assertEquals(ne.getElementName(), el.getElementName());
		}
	}

	@Test
	public final void testFindAll() {
		List<NameElement> found = dao.findName(name);
		assertNotNull(found);
		assertTrue(found.size() > 0);
	}

	@Test
	public final void testFindByEType() {
		List<NameElement> found = dao.findByEType(etype);
		assertNotNull(found);
		assertTrue(found.size() > 0);
		for (NameElement ne : found) {
			assertEquals(ne.getEtype(), el.getEtype());
		}
	}

	@Test
	public final void testFindByNameEType() {
		NameElement found = dao.findByNameEType(name, etype);
		assertNotNull(found);
		assertEquals(found.getElementName(), el.getElementName());
		assertEquals(found.getEtype(), el.getEtype());
	}

	@Test
	@Rollback(true)
	public final void testDeleteAll() {
		dao.deleteAll();
		List<NameElement> found = dao.findName(name);
		assertTrue(found == null || found.size() == 0);
	}

}
