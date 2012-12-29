package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.EntityDAO;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.EtypeManager;

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
		return this.entityDao.save(e);
	}

	@Override
	public NamedEntity find(int id) {
		return this.entityDao.findById(id);
	}

	@Override
	public List<NamedEntity> find(String url, String name) {
		if (name != null) {
			return this.entityDao.findByNameUrl(name, url);
		} else {
			return this.entityDao.findByUrl(url);
		}
	}

	@Override
	public List<NamedEntity> find(String name) {
		return this.entityDao.findByName(name);
	}

	@Override
	public List<NamedEntity> find(String name, EType etype) {
		return this.entityDao.findByNameEtype(name, etype);
	}

	@Override
	public List<NamedEntity> find(EType etype) {
		return this.entityDao.findByEtype(etype);
	}

	@Autowired
	public void setEntityDao(EntityDAO entityDao) {
		this.entityDao = entityDao;
	}

	@Autowired
	public void setEtypeManager(EtypeManager etypeManager) {
	}

}
