package it.unitn.disi.sweb.names.service.impl;

import java.util.List;

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

	@Override
	public NamedEntity find(int id) {
		return entityDao.findById(id);
	}

	@Override
	public List<NamedEntity> find(String url, String name) {
		if (name != null) {
			return entityDao.findByNameUrl(name, url);
		} else
			return entityDao.findByUrl(url);
	}

	@Override
	public List<NamedEntity> find(String name) {
		return entityDao.findByName(name);
	}

	@Override
	public List<NamedEntity> find(String name, EType etype) {
		return entityDao.findByNameEtype(name, etype);
	}

	@Override
	public List<NamedEntity> find(EType etype) {
		return entityDao.findByEtype(etype);
	}

}
