package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordType;
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
public class TestTriggerWordDAO extends TestCase {

	@Autowired
	TriggerWordDAO dao;
	@Autowired
	ElementManager elManager;
	@Autowired
	EtypeManager etypeManager;

	private TriggerWord t;
	private int id;
	private EType etype;
	private String triggerWord = "test";

	@Override
	@Before
	public void setUp() throws Exception {
		etype = etypeManager.getEtype(EtypeName.LOCATION);

		t = new TriggerWord(triggerWord, elManager.findTriggerWordType(
				"Toponym", etype));

		t = dao.save(t);
		id = t.getId();
	}

	@Override
	@After
	public void tearDown() throws Exception {
//		dao.delete(t);
	}

	@Test
	@Rollback(true)
	public final void testSave() {
		TriggerWordType tipo = elManager.findTriggerWordType("Designator",
				etypeManager.getEtype(EtypeName.ORGANIZATION));

		TriggerWord tw = new TriggerWord("test", tipo);

		checkEquals(tw, dao.save(tw));
	}

	@Test
	public final void testUpdate() {
		t.setTriggerWord("TESTING");
		checkEquals(t, dao.update(t));

		t.setType(elManager.findTriggerWordType("Designator",
				etypeManager.getEtype(EtypeName.ORGANIZATION)));
		checkEquals(t, dao.update(t));
	}

	// @Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindById() {
		checkEquals(t, dao.findById(id));
	}

	@Test
	public final void testFindByTriggerWord() {
		List<TriggerWord> list = dao.findByTriggerWord(triggerWord);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (TriggerWord tw : list) {
			assertNotNull(tw);
			assertEquals(tw.getTriggerWord(), triggerWord);
		}
	}

	@Test
	public final void testFindByTriggerWordEtype() {
		List<TriggerWord> list = dao.findByTriggerWordEtype(triggerWord, etype);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (TriggerWord tw : list) {
			assertNotNull(tw);
			assertEquals(tw.getTriggerWord(), triggerWord);
			assertEquals(tw.getType().geteType(), etype);
		}
	}

	@Test
	@Rollback(true)
	public final void testFindVariations() {
		TriggerWordType type = elManager.findTriggerWordType("Toponym", etype);

		TriggerWord variation = new TriggerWord("testVar", type);
		variation = dao.save(variation);

		t.addVariation(variation);
		t = dao.update(t);

		List<TriggerWord> list = dao.findVariations(t);
		assertNotNull(list);
		assertTrue(list.size() > 0);

		t.addVariation(t);
		t = dao.update(t);

		list = dao.findVariations(t);
		assertNotNull(list);
		assertTrue(list.size() > 0);


		list = dao.findVariations(variation);
		assertNotNull(list);
		assertTrue(list.size() > 0);
	}

	@Test
	@Rollback(true)
	public final void testIsVariation() {
		TriggerWordType type = elManager.findTriggerWordType("Toponym", etype);

		TriggerWord variation = new TriggerWord("testVar", type);
		variation = dao.save(variation);

		t.addVariation(variation);
		t = dao.update(t);

		assertTrue(dao.isVariation(triggerWord, "testVar"));
		assertTrue(dao.isVariation("testVar", triggerWord));
		assertFalse(dao.isVariation(triggerWord, "xxxx"));
		assertFalse(dao.isVariation(triggerWord, null));

	}

//	@Test
	public final void testDeleteAll() {
		fail("Not yet implemented"); // TODO
	}

	private void checkEquals(TriggerWord source, TriggerWord target) {
		assertNotNull(target);
		assertEquals(source.getTriggerWord(), target.getTriggerWord());
		assertEquals(source.getType(), target.getType());
	}

}
