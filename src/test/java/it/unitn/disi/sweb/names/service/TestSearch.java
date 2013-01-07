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
public class TestSearch extends TestCase {

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

	@Test
	public void testSearchEquals1() {
		assertEquals(null, searchEquals(null));
	}

	@Test
	public void testSearchEquals2() {
		assertEquals(null, searchEquals(""));
	}

	@Test
	public void testSearchEquals3() {
		assertEquals(null, searchEquals("Firenze"));
	}

	@Test
	public void testSearchEquals4() {
		Map<NamedEntity, Double> result = searchEquals("Giunchiglia");
		assertNotNull(result);
		assertTrue(result.size() == 1);

		for (NamedEntity e : result.keySet()) {
			assertNotNull(e);
			assertEquals(p1, e);
		}
	}

	@Test
	public void testSearchEquals5() {
		Map<NamedEntity, Double> result = searchEquals("Prof Giunchiglia");
		assertNotNull(result);
		assertTrue(result.size() == 1);

		for (NamedEntity e : result.keySet()) {
			assertNotNull(e);
			assertEquals(p1, e);
			assertEquals(1.0, result.get(e));
		}
	}

	@Test
	public void testSearchEquals6() {
		Map<NamedEntity, Double> result = searchEquals("Giunchiglia Fausto");
		assertNull(result);
	}

	/*
	 * GENERATE TOKEN
	 */

	@Test
	public void testSearchReordered1() {
		assertEquals(null, searchReordered(null));
	}
	@Test
	public void testSearchReordered2() {
		String[] tokens = new String[0];
		assertEquals(null, searchReordered(tokens));
	}

	@Test
	public void testSearchReordered3() {
		String[] tokens = {"Fausto", "Giunchiglia"};
		Map<NamedEntity, Double> result = searchReordered(tokens);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		for (NamedEntity e : result.keySet()) {
			assertEquals(p1, e);
		}
	}

	@Test
	public void testSearchReordered4() {
		String[] tokens = {"Prof", "Giunchiglia"};
		Map<NamedEntity, Double> result = searchReordered(tokens);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		for (NamedEntity e : result.keySet()) {
			assertEquals(p1, e);
		}
	}
	@Test
	public void testSearchReordered5() {
		String[] tokens = {"Giunchiglia", "Prof"};
		Map<NamedEntity, Double> result = searchReordered(tokens);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		for (NamedEntity e : result.keySet()) {
			assertEquals(p1, e);
		}
	}
	@Test
	public void testSearchReordered6() {
		String[] tokens = {"Giunchiglia", "Fausto"};
		Map<NamedEntity, Double> result = searchReordered(tokens);
		assertNotNull(result);
		assertTrue(result.size() > 0);
		for (NamedEntity e : result.keySet()) {
			assertEquals(p1, e);
		}
	}
	@Test
	public void testSearchReordered7() {
		String[] tokens = {"Giunchiglia"};
		Map<NamedEntity, Double> result = searchReordered(tokens);
		assertNull(result);
	}

	@Test
	public void testSearchMisspellings1() {
		assertEquals(null, searchMisspellings(null));
	}

	@Test
	public void testSearchMisspellings2() {
		assertEquals(null, searchMisspellings(""));
	}

	@Test
	public void testSearchMisspellings4() {
		assertEquals(null, searchMisspellings("AA"));
	}

	@Test
	public void testSearchMisspellings5() {
		assertEquals(null, searchMisspellings("AAAAAAAAA"));
	}

	@Test
	public void testSearchMisspellings6() {
		Map<NamedEntity, Double> result = searchMisspellings("Fausto Giunchiglia");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		for (NamedEntity e : result.keySet()) {
			assertEquals(p1, e);
		}
	}

	@Test
	public void testSearchMisspellings7() {
		assertEquals(null, searchMisspellings("Giunchiglia Fausto"));
	}

	@Test
	public void testSearchMisspellings8() {
		assertEquals(null, searchMisspellings("Fausto"));
	}

	@Test
	public void testSearchMisspellings9() {
		// 4938 vs 5253
		Map<NamedEntity, Double> result = searchMisspellings("Fasuto GIunichiglia");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		for (NamedEntity e : result.keySet()) {
			assertEquals(p1, e);
		}
	}

	@Test
	public void testSearchMisspellings10() {
		assertEquals(null, searchMisspellings("Fausot Gianpaolo"));
	}

	@Test
	public void testSearchMisspellings11() {
		assertEquals(null, searchMisspellings("Fasuto Marangoni"));
	}

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
		String[] input = {"pazza"};
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
		assertEquals(2, result.size());
	}

	private final Map<NamedEntity, Double> searchEquals(String input) {
		return invokePrivateMethod(input, String.class, "searchEquals");
	}

	private final Map<NamedEntity, Double> searchReordered(String[] input) {
		return invokePrivateMethod(input, String[].class, "searchReordered");
	}

	private final Map<NamedEntity, Double> searchMisspellings(String input) {
		return invokePrivateMethod(input, String.class, "searchMisspellings");
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
