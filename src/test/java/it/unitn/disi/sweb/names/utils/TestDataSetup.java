package it.unitn.disi.sweb.names.utils;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameManager;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class TestDataSetup {

	@Autowired
	NameManager nameManager;
	@Autowired
	EntityManager entityManager;

	@Autowired
	EtypeManager etypeManager;

	@Test
	public void nothing() {
		
	}
	
//	@Test
	public void testNewFullNames() {
		String[] names = { "Stella Margonar", "Dott.ssa Stella Margonar",
				"Sig.ra Stella Jr.", "Marietto" };
		String description = "https://www.facebook.com/stella.margonar";
		createEntity(names, EtypeName.PERSON, description);

		String[] names4 = { "Fausto Giunchiglia", "Professor Giunchiglia" };
		String url4 = "http://disi.unitn.it/~fausto/";

		createEntity(names4, EtypeName.PERSON, url4);
	}

//	@Test
	public void testNewEntities() {
		String[] names1 = { "Garda", "Citta' Garda" };
		String url1 = "http://www.comunedigarda.it";

		String[] names2 = { "Lago di Garda", "Garda" };
		String url2 = "http://it.wikipedia.org/wiki/Lago_di_Garda";

		String[] names3 = { "Garda Lake" };
		String url3 = "http://en.wikipedia.org/wiki/Lake_Garda";
		EtypeName type = EtypeName.LOCATION;

		createEntity(names1, type, url1);
		createEntity(names2, type, url2);
		createEntity(names3, type, url3);

	}

	private void createEntity(String[] names, EtypeName etype, String url) {
		NamedEntity ne = null;
		List<NamedEntity> list = entityManager.find(url, names[0]);
		if (list != null && !list.isEmpty())
			ne = list.get(0);
		else
			ne = entityManager.createEntity(etypeManager.getEtype(etype), url);

		for (String name : names)
			if (name != null)
				nameManager.createFullName(name, ne);
	}
}
