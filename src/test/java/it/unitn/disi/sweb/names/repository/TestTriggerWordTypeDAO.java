package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;

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
public class TestTriggerWordTypeDAO extends TestCase {

	@Autowired
	TriggerWordTypeDAO dao;
	@Autowired
	EtypeManager etypeManager;

	private TriggerWordType type;
	private String typeName = "test";
	private EType etype;

	@Override
	@Before
	public void setUp() throws Exception {
		type = new TriggerWordType();
		type.setType(typeName);
		type.setComparable(true);

		etype = etypeManager.getEtype(EtypeName.PERSON);
		type.seteType(etype);

		type = dao.save(type);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public final void testSave() {
		TriggerWordType t = new TriggerWordType();
		t.setType("testSave");
		t.seteType(etypeManager.getEtype(EtypeName.LOCATION));
		t.setComparable(false);

		checkEquals(t, dao.save(t));
	}

	@Test
	public final void testUpdate() {
		type.setType("testUpdate");
		checkEquals(type, dao.update(type));

		type.seteType(etypeManager.getEtype(EtypeName.ORGANIZATION));
		checkEquals(type, dao.update(type));
	}

	// @Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindById() {
		checkEquals(type, dao.findById(type.getId()));
	}

	@Test
	public final void testFindAll() {
		List<TriggerWordType> all = dao.findAll();
		assertNotNull(all);
		assertTrue(all.size() > 0);
		for (TriggerWordType t : all) {
			assertNotNull(t);
		}
	}

	@Test
	public final void testFindByName() {
		List<TriggerWordType> all = dao.findByName(typeName);
		assertNotNull(all);
		assertTrue(all.size() > 0);
		for (TriggerWordType t : all) {
			assertNotNull(t);
			assertEquals(typeName, t.getType());
		}
	}

	@Test
	public final void testFindByEType() {
		List<TriggerWordType> all = dao.findByEType(etype);
		assertNotNull(all);
		assertTrue(all.size() > 0);
		for (TriggerWordType t : all) {
			assertNotNull(t);
			assertEquals(etype, t.geteType());
		}
	}

	@Test
	public final void testFindByNameEType() {
		checkEquals(type, dao.findByNameEType(typeName, etype));
	}

//	@Test
	public final void testDeleteAll() {
		fail("Not yet implemented"); // TODO
	}

	private void checkEquals(TriggerWordType source, TriggerWordType target) {
		assertNotNull(target);
		assertEquals(source.getType(), target.getType());
		assertEquals(source.geteType(), source.geteType());
		assertEquals(source.getComparable(), target.getComparable());
	}

}
