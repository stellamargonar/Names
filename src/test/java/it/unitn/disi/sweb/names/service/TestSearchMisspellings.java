package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.service.impl.NameSearchImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;

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
public class TestSearchMisspellings extends TestCase {

	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;

	@Autowired
	NameSearch nameSearch;

	private NamedEntity p1;

	@Override
	@Before
	public void setUp() throws Exception {
		p1 = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.PERSON),
				"http://disi.unitn.it/~fausto/");
		FullName f1 = nameManager.createFullName("Prof Giunchiglia", p1);
		FullName f2 = nameManager.createFullName("Fausto Giunchiglia", p1);

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

	private final Map<NamedEntity, Double> searchMisspellings(String input) {
		return invokePrivateMethod(input, String.class, "searchMisspellings");
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
