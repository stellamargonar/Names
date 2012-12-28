package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordTypeDAO;
import it.unitn.disi.sweb.names.service.ElementManager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("elementManager")
public class ElementManagerImpl implements ElementManager{

	@Autowired
	NameElementDAO nameDao;
	@Autowired
	TriggerWordTypeDAO twDao;
	
	@Override
	public List<NameElement> findNameElement(EType etype) {
		return nameDao.findByEType(etype);
	}

	@Override
	public List<TriggerWordType> findTriggerWordType(EType etype) {
		return twDao.findByEType(etype);
	}

}
