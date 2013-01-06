package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.Prefix;
import it.unitn.disi.sweb.names.repository.PrefixDAO;
import it.unitn.disi.sweb.names.utils.Pair;

import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testApplicationContext.xml"})
public class TestPrefixManager extends TestCase {

	@Autowired
	PrefixManager manager;

	@Autowired
	PrefixDAO dao;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	NameManager nameManager;

	private String query = "te";
	private EType etype;

	@Override
	@Before
	public void setUp() throws Exception {
		etype = etypeManager.getEtype(EtypeName.PERSON);
		NamedEntity entity = entityManager.createEntity(etype, "test");

		FullName f1 = nameManager.createFullName("Test", entity);
		Prefix p1 = new Prefix("te", f1);
		dao.save(p1);

		FullName f2 = nameManager.createFullName("Testing", entity);
		Prefix p2 = new Prefix("te", f2);
		dao.save(p2);

		NamedEntity entity1 = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION), "test");
		FullName f3 = nameManager.createFullName("Test", entity1);
		Prefix p3 = new Prefix("te", f3);
		dao.save(p3);
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

//	@Test
	public final void testUpdatePrefixes() {
		// TODO : first empty db, then test the function
		fail("Not yet implemented");
	}

	@Test
	public final void testSearchString() {
		List<Pair<FullName, Double>> result = manager.search(query);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		for (Pair<FullName, Double> p : result) {
			assertNotNull(p);
			assertNotNull(p.key);
			assertTrue(manager.normalize(p.key.getName()).startsWith(query)
					|| manager.normalize(p.key.getName()).contains(" " + query));
		}


		result = manager.search(null);
		assertNotNull(result);
		assertTrue(result.size() == 0);

		// unexisting prefix
		result = manager.search("zzzz");
		assertNotNull(result);
		assertTrue(result.size() == 0);



	}

	@Test
	public final void testSearchStringEType() {
		List<Pair<FullName, Double>> result = manager.search(query, etype);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		for (Pair<FullName, Double> p : result) {
			assertNotNull(p);
			assertNotNull(p.key);
			assertTrue(manager.normalize(p.key.getName()).startsWith(query)
					|| manager.normalize(p.key.getName()).contains(" " + query));
			assertEquals(etype, p.key.getEntity().getEType());
		}

	}

	@Test
	public final void testNormalize() {
		String input = "";
		String expected = "";
		String normalized = manager.normalize(input);
		assertEquals(expected, normalized);

		input = "a"; expected = "a";
		normalized = manager.normalize(input);
		assertEquals(expected, normalized);

		input = "A"; expected = "a";
		normalized = manager.normalize(input);
		assertEquals(expected, normalized);

		input = "à"; expected = "a";
		normalized = manager.normalize(input);
		assertEquals(expected, normalized);

		input = "Ž"; expected = "z";
		normalized = manager.normalize(input);
		assertEquals(expected, normalized);
	}

}
