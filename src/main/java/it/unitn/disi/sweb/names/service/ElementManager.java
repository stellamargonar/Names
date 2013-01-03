package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.TriggerWordType;

import java.util.List;

public interface ElementManager {

	List<NameElement> findNameElement(EType etype);
	NameElement findNameElement(String element, EType etype);

	List<TriggerWordType> findTriggerWordType(EType etype);
	TriggerWordType findTriggerWordType(String type, EType etype);

}
