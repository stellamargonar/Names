package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.EntityDAO;
import it.unitn.disi.sweb.names.service.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("entityManager")
public class EntityManagerImpl implements EntityManager {

	@Autowired
	EntityDAO entityDao;

	@Override
	public NamedEntity createEntity(EType etype, String url) {
		if (etype == null)
			return null;

		NamedEntity e = new NamedEntity();
		e.setEType(etype);
		e.setUrl(url);
		return entityDao.save(e);
	}

}
