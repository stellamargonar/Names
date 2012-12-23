package it.unitn.disi.sweb.names.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.repository.ETypeDAO;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;

@Component("etypemanager")
public class EtypeManagerImpl implements EtypeManager {

	@Autowired
	ETypeDAO etypeDao;

	@Override
	public EType getEtype(EtypeName type) {
		String name = "";
		switch (type) {
		case PERSON:
			name = "Person";
			break;
		case LOCATION:
			name = "Location";
			break;
		case ORGANIZATION:
			name = "Organization";
			break;
		case EVENT:
			name = "Event";
			break;
		default:
			return null;
		}
		return etypeDao.findByName(name);
	}

}
