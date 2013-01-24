package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.EntityDAO;
import it.unitn.disi.sweb.names.repository.FullNameDAO;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.NameManager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("entityManager")
public class EntityManagerImpl implements EntityManager {

	private EntityDAO entityDao;

	private NameManager nameManager;
	private FullNameDAO nameDao;

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
		List<FullName> names = nameDao.findByName(name.toLowerCase());
		List<NamedEntity> entities = new ArrayList<>();
		for (FullName n : names) {
			entities.add(entityDao.findById(n.getGUID()));
		}
		return entities;
	}

	@Override
	public List<NamedEntity> find(String name, EType etype) {
		List<FullName> names = nameDao.findByNameEtype(name.toLowerCase(),
				etype);
		List<NamedEntity> entities = new ArrayList<>();
		for (FullName n : names) {
			entities.add(entityDao.findById(n.getGUID()));
		}
		return entities;
	}

	@Override
	public List<NamedEntity> find(EType etype) {
		return entityDao.findByEtype(etype);
	}

	@Override
	public void deleteEntity(NamedEntity e) {
		if (e != null) {
			List<FullName> names = nameManager.find(e);
			for (FullName f : names) {
				nameDao.delete(f);
			}
		}
		e = entityDao.update(e);
		entityDao.delete(e);
	}

	@Autowired
	public void setEntityDao(EntityDAO entityDao) {
		this.entityDao = entityDao;
	}

	@Autowired
	public void setNameManager(NameManager nameManager) {
		this.nameManager = nameManager;
	}

	@Autowired
	public void setNameDao(FullNameDAO nameDao) {
		this.nameDao = nameDao;
	}

}
