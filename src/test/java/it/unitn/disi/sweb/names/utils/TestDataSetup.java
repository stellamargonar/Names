package it.unitn.disi.sweb.names.utils;

import java.util.List;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.ETypeDAO;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.NameManager;

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
	ETypeDAO etypeDao;

	@Test
	public void testNewFullNames() {
		String etype = "Person";
		String name1 = "Stella Margonar";
		String name2 = "Dott.ssa Stella Margonar";
		String name3 = "Sig.ra Stella Jr.";
		String nickname = "Marietto";

		String description = "https://www.facebook.com/stella.margonar";

		EType e = etypeDao.findByName(etype);

		NamedEntity ne = null;
		List<NamedEntity> list = entityManager.find(description, name1);
		if (list != null && !list.isEmpty())
			ne = list.get(0);
		else
			ne = entityManager.createEntity(e, description);
		
		nameManager.createFullName(nickname, ne);

		nameManager.createFullName(name1, ne);
		nameManager.createFullName(name2, ne);
		nameManager.createFullName(name3, ne);
	}
}
