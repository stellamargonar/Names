package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.IndividualName;
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
public class TestIndividualNameDAO extends TestCase {

	@Autowired
	IndividualNameDAO dao;

	@Autowired
	EtypeManager etypeManager;
	@Autowired
	ElementManager elManager;

	private IndividualName i;
	private String name = "testname";
	private EType etype;
	private int id;
	private String translation = "testTranslation";
	private String nameElement = "FamilyName";

	@Override
	@Before
	public void setUp() throws Exception {
		etype = etypeManager.getEtype(EtypeName.PERSON);

		IndividualName t = new IndividualName();
		t.setName(translation);
		t.setFrequency(0);
		t.setNameElement(elManager.findNameElement(nameElement, etype));
		t = dao.save(t);

		i = new IndividualName();
		i.setName(name);
		i.setFrequency(0);
		i.setNameElement(elManager.findNameElement(nameElement, etype));

		i.addTranslation(t);
		id = dao.update(i).getId();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		dao.delete(i);
	}

	@Test
	@Rollback(true)
	public final void testSave() {
		IndividualName n = new IndividualName();
		n.setName("saveTest");
		n.setFrequency(0);
		n.setNameElement(elManager.findNameElement(nameElement, etype));

		IndividualName returned = dao.save(n);
		assertNotNull(returned);
		assertEquals(returned.getName(), n.getName());
		assertEquals(returned.getNameElement(), n.getNameElement());
	}

	@Test
	@Rollback(true)
	public final void testUpdate() {
		i.setName("updateTest");
		IndividualName returned = dao.update(i);
		assertNotNull(returned);
		assertEquals(returned.getName(), i.getName());
		assertEquals(returned.getNameElement(), i.getNameElement());
		i = returned;
	}

	// @Test
	public final void testDelete() {
		System.out.println(i);
		dao.delete(i);
		System.out.println(id + ": " + dao.findById(id));
		assertNull(dao.findById(id));
	}

	@Test
	public final void testFindById() {
		System.out.println(i.getName());
		IndividualName found = dao.findById(id);
		assertNotNull(found);
		assertEquals(found.getName(), i.getName());
		assertEquals(found.getNameElement(), i.getNameElement());
	}

	@Test
	public final void testFindByName() {
		List<IndividualName> found = dao.findByName(name);
		assertNotNull(found);
		assertTrue(found.size() > 0);
		for (IndividualName n : found) {
			assertEquals(n.getName(), i.getName());
		}
	}
	@Test
	public final void testFindByNameEtype() {
		List<IndividualName> found = dao.findByNameEtype(name, etype);
		assertNotNull(found);
		assertTrue(found.size() > 0);
		for (IndividualName n : found) {
			assertEquals(n.getName(), i.getName());
			assertEquals(n.getNameElement().getEtype(), etype);
		}
	}

	@Test
	public final void testIsTranslation() {
		assertTrue(dao.isTranslation(name, translation));
		assertFalse(dao.isTranslation(name, translation + "x"));
	}

	@Test
	public final void testFindTranslations() {
		List<IndividualName> found = dao.findTranslations(i);
		assertNotNull(found);
		assertTrue(found.size() == 1);
		assertEquals(found.get(0).getName(), translation);

	}

}
