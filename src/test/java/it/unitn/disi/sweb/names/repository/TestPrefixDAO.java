package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.Prefix;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@Transactional(rollbackFor = Throwable.class)
public class TestPrefixDAO extends TestCase {

	@Autowired
	PrefixDAO dao;
	@Autowired
	NameManager nameManager;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	EntityManager entityManager;

	private Prefix p;
	private FullName fullName;
	private int id;
	private String prefix = "te";

	@Override
	@Before
	public void setUp() throws Exception {
		NamedEntity entity = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.PERSON), "");
		fullName = nameManager.createFullName("Testing", entity);

		p = new Prefix();
		p.setPrefix(prefix);
		p.setSelected(fullName);
		p.setFrequency(10);

		p = dao.save(p);
		id = p.getId();
	}
	@Override
	@After
	public void tearDown() throws Exception {
		// dao.delete(p);
	}

	@Test
	public final void testSave() {
		Prefix n = new Prefix();
		n.setPrefix("tes");
		n.setSelected(fullName);
		n.setFrequency(100);

		checkEquals(n, dao.save(n));
	}

	@Test
	public final void testUpdate() {
		p.setPrefix("test");
		checkEquals(p, dao.update(p));
	}

	// @Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindById() {
		checkEquals(p, dao.findById(id));
	}

	@Test
	public final void testFindByPrefix() {
		List<Prefix> found = dao.findByPrefix(prefix);
		assertNotNull(found);
		assertTrue(found.size() > 0);
		for (Prefix f : found) {
			assertNotNull(f);
			assertEquals(f.getPrefix(), p.getPrefix());
		}
	}

	@Test
	public final void testFindByPrefixSelected() {
		checkEquals(p, dao.findByPrefixSelected(prefix, fullName));
	}

	private void checkEquals(Prefix source, Prefix target) {
		assertNotNull(target);
		assertEquals(source.getPrefix(), target.getPrefix());
		assertEquals(source.getSelected(), target.getSelected());
		assertEquals(source.getFrequency(), target.getFrequency());
	}
}
