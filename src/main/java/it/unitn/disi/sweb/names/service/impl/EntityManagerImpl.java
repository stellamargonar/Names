package it.unitn.disi.sweb.names.service.impl;

import java.util.List;

import javax.persistence.EnumType;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.ETypeDAO;
import it.unitn.disi.sweb.names.repository.EntityDAO;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("entityManager")
public class EntityManagerImpl implements EntityManager {

	@Autowired
	EntityDAO entityDao;
	@Autowired
	EtypeManager etypeManager;
	@Autowired
	NameManager nameManager;
	
	
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

	public NamedEntity parse(String line) {
		NamedEntity entity = new NamedEntity();

		String[] tokens = line.split(";");
		String names = tokens[0];
		String x = "[Garda {(Garda=ProperNoun)}], [Citta' di Garda {(Garda,ProperNoun) (Citta', Toponym)}]";
		// -- name parse
		names = names.substring(1, names.length()-1);
		String[] name = names.split(","); // [Garda={(Garda:ProperNoun)}]
		for (String n: name) {
			n = n.substring(1, n.length()-1); //Garda={(Garda:ProperNoun)}
			String[] element = n.split("=");
			String fullname = element[0];
			String[] nameTokens = element[1].substring(1, element[1].length()-1).split(" ");
			for (String t: nameTokens) {
				// TODO finire
			}
			
		}
		
		//
		
		
		EtypeName type = EtypeName.valueOf(tokens[1]);
		String url = tokens[2];
		entity.setEType(etypeManager.getEtype(type));
		entity.setUrl(url);
		return entity;
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
