package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NamedEntity;

public interface NameCreation {

	public FullName createFullName(String name, NamedEntity en) ;
	
	public NamedEntity createEntity(EType etype);

	public void createIndividualName(String string, NameElement el);
}
