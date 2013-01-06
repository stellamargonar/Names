package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.repository.ETypeDAO;
import it.unitn.disi.sweb.names.service.EtypeManager;
import it.unitn.disi.sweb.names.service.EtypeName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("etypemanager")
public class EtypeManagerImpl implements EtypeManager {

	private ETypeDAO etypeDao;

	@Override
	public EType getEtype(EtypeName type) {
		if (type == null) {
			return null;
		}
		String name = "";
		switch (type) {
			case PERSON :
				name = "Person";
				break;
			case LOCATION :
				name = "Location";
				break;
			case ORGANIZATION :
				name = "Organization";
				break;
		}
		return etypeDao.findByName(name);
	}

	@Autowired
	public void setEtypeDao(ETypeDAO etypeDao) {
		this.etypeDao = etypeDao;
	}
}
