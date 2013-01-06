package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.service.EntityManager;
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
public class TestFullNameDAO extends TestCase {

	@Autowired
	FullNameDAO dao;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;

	private FullName fullName;
	private NamedEntity entity;
	private String name = "test prova";
	private String nameNormalized = name;
	private String nameToCompare = name;

	@Override
	@Before
	public void setUp() throws Exception {
		entity = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION), "test");

		fullName = new FullName();
		fullName.setEntity(entity);
		fullName.setName(name);
		fullName.setNameNormalized(nameNormalized);
		fullName.setNameToCompare(nameToCompare);

		fullName = dao.save(fullName);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(true)
	public final void testSave() {
		FullName f = new FullName();
		f.setEntity(entity);
		f.setName("TestSave");

		checkEquals(f, dao.save(f));
	}

	@Test
	public final void testUpdate() {
		fullName.setName("Testupdate");
		checkEquals(fullName, dao.update(fullName));

		NamedEntity e = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.PERSON), "Testupdate");
		fullName.setEntity(e);
		checkEquals(fullName, dao.update(fullName));
	}

	// @Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindById() {
		checkEquals(fullName, dao.findById(fullName.getId()));
	}
	@Test
	public final void testFindByName() {
		List<FullName> list = dao.findByName(name);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (FullName f : list) {
			assertNotNull(f);
			assertEquals(name, f.getName());
		}
	}

	@Test
	public final void testFindByNameNormalized() {
		List<FullName> list = dao.findByNameNormalized(nameNormalized);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (FullName f : list) {
			assertNotNull(f);
			assertEquals(nameNormalized, f.getNameNormalized());
		}
	}

	@Test
	public final void testFindByNameToCompare() {
		List<FullName> list = dao.findByNameToCompare(nameToCompare);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (FullName f : list) {
			assertNotNull(f);
			assertEquals(nameToCompare, f.getNameToCompare());
		}
	}

	@Test
	public final void testFindByNameEtype() {
		List<FullName> list = dao.findByNameEtype(name, entity.getEType());
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (FullName f : list) {
			assertNotNull(f);
			assertEquals(name, f.getName());
			assertEquals(entity.getEType(), f.getEntity().getEType());
		}
	}

	@Test
	public final void testFindByEntity() {
		List<FullName> list = dao.findByEntity(entity);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (FullName f : list) {
			assertNotNull(f);
			assertEquals(entity, f.getEntity());
		}
	}

	@Test
	public final void testFindByEntityName() {
		checkEquals(fullName, dao.findByEntityName(name, entity));
		assertNull(dao.findByEntityName(name + "x", entity));
	}

	@Test
	public final void testFindVariant() {
		String nameVariant = "testVariant";
		FullName variant = new FullName();
		variant.setEntity(entity);
		variant.setName(nameVariant);

		variant = dao.save(variant);

		List<FullName> variants = dao.findVariant(name, entity.getEType());
		assertNotNull(variants);
		assertTrue(variants.size() > 0);
		for (FullName v : variants) {
			System.out.println(v.getName());
			checkEquals(variant, v);
		}
	}

	@Test
	public final void testFindByToken() {
		String token = "te";
		List<FullName> list = dao.findByToken(token);
		assertNotNull(list);
		assertTrue(list.size() > 0);

		boolean found = false;
		for (FullName n : list) {
			assertNotNull(n);
			assertTrue(n.getName().contains(" " + token)
					|| n.getName().startsWith(token));
			if (n.getId() == fullName.getId()) {
				found = true;
			}
		}
		assertTrue(found);

	}

	@Test
	public final void testFindByNgram() {
		int ngram = 335;
		int diff = 10;

		fullName.setnGramCode(ngram);
		fullName = dao.update(fullName);

		List<FullName> list = dao.findByNgram(ngram, diff);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (FullName f : list) {
			assertTrue(Math.abs(f.getnGramCode() - ngram) <= diff);
		}
	}
	private void checkEquals(FullName source, FullName target) {
		assertNotNull(target);
		assertEquals(source.getName(), target.getName());
		assertEquals(source.getEntity(), target.getEntity());
	}
}
