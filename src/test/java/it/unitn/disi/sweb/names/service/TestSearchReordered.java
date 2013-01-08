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
public class TestSearchReordered extends TestCase {

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

	private final Map<NamedEntity, Double> searchReordered(String[] input) {
		return invokePrivateMethod(input, String[].class, "searchReordered");
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
