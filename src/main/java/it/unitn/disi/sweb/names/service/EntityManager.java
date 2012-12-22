package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;

public interface EntityManager {
	
	/**
	 * creates a new entity with etype and description setted
	 * 
	 * @param etype
	 * @param url
	 * @return entity persisted in the database
	 */
	public NamedEntity createEntity(EType etype, String url);

}
