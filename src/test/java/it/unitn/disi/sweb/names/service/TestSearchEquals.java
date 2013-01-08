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
public class TestSearchEquals extends TestCase {

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

	private final Map<NamedEntity, Double> searchEquals(String input) {
		return invokePrivateMethod(input, String.class, "searchEquals");
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
