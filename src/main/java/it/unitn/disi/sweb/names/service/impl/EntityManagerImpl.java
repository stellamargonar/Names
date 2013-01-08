package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.EntityDAO;
import it.unitn.disi.sweb.names.service.EntityManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("entityManager")
public class EntityManagerImpl implements EntityManager {

	private EntityDAO entityDao;
	@Override
	public NamedEntity createEntity(EType etype, String url) {
		if (etype == null) {
			return null;
		}

		NamedEntity e = new NamedEntity();
		e.setEType(etype);
		e.setUrl(url);
		return entityDao.save(e);
	}

	@Override
	public NamedEntity find(int id) {
		return entityDao.findById(id);
	}

	@Override
	public List<NamedEntity> find(String url, String name) {
		if (name != null && url != null) {
			return entityDao.findByNameUrl(name.toLowerCase(), url);
		} else if (name != null) {
			return entityDao.findByName(name.toLowerCase());
		} else {

			return entityDao.findByUrl(url);
		}
	}
	@Override
	public List<NamedEntity> find(String name) {
		return entityDao.findByName(name.toLowerCase());
	}

	@Override
	public List<NamedEntity> find(String name, EType etype) {
		return entityDao.findByNameEtype(name.toLowerCase(), etype);
	}

	@Override
	public List<NamedEntity> find(EType etype) {
		return entityDao.findByEtype(etype);
	}

	@Autowired
	public void setEntityDao(EntityDAO entityDao) {
		this.entityDao = entityDao;
	}

}
