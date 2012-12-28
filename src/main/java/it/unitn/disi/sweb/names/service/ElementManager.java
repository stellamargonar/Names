package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.TriggerWordType;

import java.util.List;

public interface ElementManager {

	public List<NameElement> findNameElement(EType etype);
	
	public List<TriggerWordType> findTriggerWordType(EType etype);
	
}
