package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.service.impl.NameSearchImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

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
public class TestSearchToken extends TestCase {

	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;

	@Autowired
	NameSearch nameSearch;

	private NamedEntity e1;
	private NamedEntity e2;
	private NamedEntity e3;
	private NamedEntity p1;
	@Override
	@Before
	public void setUp() throws Exception {
		p1 = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.PERSON),
				"http://disi.unitn.it/~fausto/");
		FullName f1 = nameManager.createFullName("Prof Giunchiglia", p1);
		FullName f2 = nameManager.createFullName("Fausto Giunchiglia", p1);

		e1 = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION),
				"http://it.wikipedia.org/wiki/Piazza_San_Pietro");
		nameManager.createFullName("Piazza San Pietro", e1);
		nameManager.createFullName("Saint Peter Square", e1);

		e2 = entityManager
				.createEntity(etypeManager.getEtype(EtypeName.LOCATION),
						"http://it.wikipedia.org/wiki/Basilica_di_San_Pietro_in_Vincoli");
		nameManager.createFullName("San Pietro in Vincoli", e2);

		e3 = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION),
				"http://it.wikipedia.org/wiki/Roma");
		nameManager.createFullName("Roma", e3);

	}
	/*
	 * SETUP
	 *
	 * stat: P.zza San Pietro, 1, 30 san pietro, 1, 10 san pietro, 3, 2
	 */

	@Override
	@After
	public void tearDown() throws Exception {
	}

	/*
	 * TOP RANK .....
	 */

	/*
	 * GENERATE TOKEN
	 */

	@Test
	public void testSearchToken1() {
		assertEquals(null, searchToken(null));
	}
	@Test
	public void testSearchToken2() {
		String[] input = new String[0];
		assertEquals(null, searchToken(input));
	}
	@Test
	public void testSearchToken3() {
		String[] input = {"AAAAAAAA"};
		assertEquals(null, searchToken(input));
	}
	@Test
	public void testSearchToken4() {
		String[] input = {"san", "pietro"};
		Map<NamedEntity, Double> result = searchToken(input);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.containsKey(e1));
		assertTrue(result.containsKey(e2));
		assertEquals(2, result.size());
	}

	@Test
	public void testSearchToken5() {
		String[] input = {"piazza"};
		Map<NamedEntity, Double> result = searchToken(input);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.containsKey(e1));
		assertFalse(result.containsKey(e2));
		assertEquals(1, result.size());
	}
	@Test
	public void testSearchToken6() {
		String[] input = {"piazza", "san", "pietro"};
		Map<NamedEntity, Double> result = searchToken(input);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.containsKey(e1));
		assertTrue(result.containsKey(e2));
		assertEquals(2, result.size());
	}

	@Test
	public void testSearchToken7() {
		String[] input = {"piaza"};

		Map<NamedEntity, Double> result = searchToken(input);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.containsKey(e1));
		assertFalse(result.containsKey(e2));
		assertEquals(1, result.size());
	}
	@Test
	public void testSearchToken8() {
		String[] input = {"petro"};
		Map<NamedEntity, Double> result = searchToken(input);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.containsKey(e1));
		assertTrue(result.containsKey(e2));
		assertEquals(2, result.size());
	}
	@Test
	public void testSearchToken9() {
		String[] input = {"santo", "petro"};
		Map<NamedEntity, Double> result = searchToken(input);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.containsKey(e1));
		assertTrue(result.containsKey(e2));
	}

	@Test
	public void testSearchToken10() {
		String[] input = {"Professor"};
		Map<NamedEntity, Double> result = searchToken(input);
		assertNotNull(result);
		assertTrue(result.size() == 1);
		assertTrue(result.containsKey(p1));
	}

	private final Map<NamedEntity, Double> searchToken(String[] input) {
		return invokePrivateMethod(input, String[].class, "searchToken");
	}

	private final Map<NamedEntity, Double> invokePrivateMethod(Object input,
			Class inputType, String methodName) {
		try {
			Method method = NameSearchImpl.class.getDeclaredMethod(methodName,
					inputType);
			method.setAccessible(true);
			return (Map<NamedEntity, Double>) method.invoke(nameSearch, input);

		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
