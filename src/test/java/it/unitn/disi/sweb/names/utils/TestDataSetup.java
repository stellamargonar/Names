package it.unitn.disi.sweb.names.utils;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.repository.ETypeDAO;
import it.unitn.disi.sweb.names.repository.EntityDAO;
import it.unitn.disi.sweb.names.repository.FullNameDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.service.NameCreation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class TestDataSetup {

	@Autowired
	NameCreation nameCreation;
	@Autowired
	ETypeDAO etypeDao;
	@Autowired
	EntityDAO entityDao;
	@Autowired
	FullNameDAO fullnameDao;
	@Autowired
	NameElementDAO nameElementDao;

	@Test
	public void testNewFullNames() {
		String etype = "Person";
		String name = "Stella Margonar";
		EType e = etypeDao.findByName(etype);
		// NamedEntity ne =
		// nameCreation.createEntity(etypeDao.findByName(etype));
		// System.out.println(ne.getGUID());
		// assertNotNull("entity not stored", entityDao.findById(ne.getGUID()));

		// nameCreation.createFullName(name, ne);
		NameElement el = nameElementDao.findByNameEType("GivenName", e);
		nameCreation.createIndividualName("stefano", el);

//		List<FullName> result = fullnameDao.findByName(name);
		// assertNotNull("name \"" + name + "\" not stored", result);
		// assertTrue("result empty", result.size() > 0);
		// assertEquals("different entitties", 1,
		// fullnameDao.findByEntityName(name, ne));
	}
}

/*
 * 
 * FullName [id=0, name=Stella Margonar,
 * entity=it.unitn.disi.sweb.names.model.NamedEntity@3ac96e25,
 * nameTokens=[NameToken [individualName=IndividualName [name=Margonar],
 * position=1], NameToken [individualName=IndividualName [name=Stella],
 * position=0]], triggerWordTokens=null]
 */
