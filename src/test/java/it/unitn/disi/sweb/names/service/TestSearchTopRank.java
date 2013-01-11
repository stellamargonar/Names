package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.repository.UsageStatisticsDAO;
import it.unitn.disi.sweb.names.service.impl.NameSearchImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
public class TestSearchTopRank extends TestCase {
	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	StatisticsManager statManager;
	@Autowired
	UsageStatisticsDAO dao;

	@Autowired
	NameSearch nameSearch;

	private NamedEntity roma;
	private NamedEntity romano;
	private NamedEntity romaCalcio;

	@Override
	@Before
	public void setUp() throws Exception {
		Logger.getLogger("org.hibernate.SQL").setLevel(Level.OFF);

		roma = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION),
				"http://it.wikipedia.org/wiki/Roma");
		FullName roma1 = nameManager.createFullName("Roma", roma);
		FullName roma2 = nameManager.createFullName("Rome", roma);

		romano = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.LOCATION),
				"http://www.comune.romano.vi.it");
		FullName romano1 = nameManager.createFullName("Romano (VI)", romano);
		FullName romano2 = nameManager.createFullName("Romano d'Ezzelino",
				romano);

		romaCalcio = entityManager.createEntity(
				etypeManager.getEtype(EtypeName.ORGANIZATION),
				"http://www.asroma.it/it/index.html");
		FullName calcio1 = nameManager.createFullName("Roma", romaCalcio);
		FullName calcio2 = nameManager.createFullName("AS Roma", romaCalcio);
		FullName calcio3 = nameManager
				.createFullName("Roma Calcio", romaCalcio);

		statManager.updateSearchStatistic("roma", roma1);
		statManager.updateSearchStatistic("roma", roma1);
		statManager.updateSearchStatistic("roma", roma1);
		statManager.updateSearchStatistic("roma", roma1);

		statManager.updateSearchStatistic("roma", calcio1);

		statManager.updateSearchStatistic("roman", roma1);
		statManager.updateSearchStatistic("roman", roma1);

		statManager.updateSearchStatistic("romano", romano1);
		statManager.updateSearchStatistic("romano", romano1);

		statManager.updateSearchStatistic("roman", romano1);

		UsageStatistic u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(roma1);
		u.setFrequency(100);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(roma2);
		u.setFrequency(90);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(calcio1);
		u.setFrequency(80);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(calcio2);
		u.setFrequency(70);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(calcio3);
		u.setFrequency(50);
		dao.save(u);

		u = new UsageStatistic();
		u.setQuery("Rom");
		u.setSelected(romano2);
		u.setFrequency(30);
		dao.save(u);
		Logger.getLogger("org.hibernate.SQL").setLevel(Level.DEBUG);
	}

	@Test
	public void testSearchTopRank1() {
		assertEquals(null, searchTopRank(null));
	}

	@Test
	public void testSearchTopRank2() {
		Map<NamedEntity, Double> result = searchTopRank("test");
		assertNull(result);
	}

	@Test
	public void testSearchTopRank3() {
		Map<NamedEntity, Double> result = searchTopRank("roma");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.containsKey(roma));
		assertTrue(result.containsKey(romaCalcio));
		assertFalse(result.containsKey(romano));
		assertTrue(result.get(roma) > result.get(romaCalcio));
	}

	@Test
	public void testSearchTopRank4() {
		Map<NamedEntity, Double> result = searchTopRank("roman");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.containsKey(roma));
		assertFalse(result.containsKey(romaCalcio));
		assertTrue(result.containsKey(romano));
		assertTrue(result.get(roma) > result.get(romano));
	}

	@Test
	public void testSearchTopRank5() {
		Map<NamedEntity, Double> result = searchTopRank("rom");
		assertNotNull(result);
		assertTrue(result.size() > 0);
		assertTrue(result.containsKey(roma));
		assertTrue(result.containsKey(romaCalcio));
		assertFalse(result.containsKey(romano));
		assertTrue(result.get(roma) > result.get(romaCalcio));
	}

	@Test
	public void testSearchTopRank6() {
		// top rank with misspellings
		Map<NamedEntity, Double> result = searchTopRank("aroma");
		assertNull(result);
	}

	private final Map<NamedEntity, Double> searchTopRank(String input) {
		return invokePrivateMethod(input, String.class, "searchTopRank");
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
