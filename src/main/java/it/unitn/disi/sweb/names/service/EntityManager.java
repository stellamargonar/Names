package it.unitn.disi.sweb.names.service;

import java.util.List;

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

	
	// FIND METHODS
	public NamedEntity find(int id);

	public List<NamedEntity> find(String url, String name);

	public List<NamedEntity> find(String name);

	public List<NamedEntity> find(String name, EType etype);

	public List<NamedEntity> find(EType etype);

}
