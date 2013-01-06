package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.service.ElementManager;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
@Transactional(rollbackFor = Throwable.class)
public class TestTriggerWordTokenDAO extends TestCase {

	@Autowired
	TriggerWordTokenDAO dao;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	NameManager nameManager;
	@Autowired
	ElementManager elManager;

	@Autowired
	TriggerWordDAO twDao;

	private TriggerWordToken token;
	private FullName fullName;
	private TriggerWord t;
	private NamedEntity entity;
	@Override
	@Before
	public void setUp() throws Exception {
		entity = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION), "www.test.com");
		fullName = nameManager.createFullName("Test", entity);

		t = new TriggerWord("Testing", elManager.findTriggerWordType("Toponym",
				etypeManager.getEtype(EtypeName.LOCATION)));
		t = twDao.save(t);

		token = new TriggerWordToken();
		token.setFullName(fullName);
		token.setTriggerWord(t);
		token.setPosition(1);
		token = dao.save(token);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(true)
	public final void testSave() {
		TriggerWordToken tok = new TriggerWordToken();
		tok.setFullName(fullName);
		tok.setTriggerWord(t);
		tok.setPosition(2);
		checkEquals(tok, dao.save(tok));
	}

	@Test
	public final void testUpload() {
		token.setPosition(2);
		checkEquals(token, dao.update(token));

		FullName name = nameManager.createFullName("TestUpdate", entity);
		token.setFullName(name);
		checkEquals(token, dao.update(token));

		TriggerWord tw = new TriggerWord("TestingUpdate",
				elManager.findTriggerWordType("Title",
						etypeManager.getEtype(EtypeName.PERSON)));
		token.setTriggerWord(tw);
		checkEquals(token, dao.update(token));
	}

	// @Test
	public final void testDelete() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testFindById() {
		checkEquals(token, dao.findById(token.getId()));
	}

	@Test
	public final void testFindByFullName() {
		List<TriggerWordToken> list = dao.findByFullName(fullName);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (TriggerWordToken t : list) {
			assertNotNull(t);
			assertEquals(fullName, t.getFullName());
		}
	}

	@Test
	public final void testFindByTriggerWord() {
		List<TriggerWordToken> list = dao.findByTriggerWord(t);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (TriggerWordToken tw : list) {
			assertNotNull(tw);
			assertEquals(t, tw.getTriggerWord());
		}
	}

	@Test
	public final void testFindByTriggerWordFullName() {
		List<TriggerWordToken> list = dao
				.findByTriggerWordFullName(t, fullName);
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (TriggerWordToken tw: list) {
			assertNotNull(tw);
			assertEquals(t, tw.getTriggerWord());
			assertEquals(fullName, tw.getFullName());
		}
	}

	@Test
	public final void testFindByFullNamePosition() {
		checkEquals(token, dao.findFullNamePosition(fullName, 1));
	}

	public void checkEquals(TriggerWordToken source, TriggerWordToken target) {
		assertNotNull(target);
		assertEquals(target.getFullName(), source.getFullName());
		assertEquals(target.getTriggerWord(), source.getTriggerWord());
		assertEquals(target.getPosition(), source.getPosition());
	}

}
