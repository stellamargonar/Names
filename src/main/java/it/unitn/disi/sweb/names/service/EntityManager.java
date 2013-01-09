package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;

public interface EntityManager {

	/**
	 * creates a new entity with etype and description setted
	 *
	 * @param etype
	 * @param url
	 * @return entity persisted in the database
	 */
	NamedEntity createEntity(EType etype, String url);

	// FIND METHODS
	NamedEntity find(int id);

	List<NamedEntity> find(String url, String name);

	List<NamedEntity> find(String name);

	List<NamedEntity> find(String name, EType etype);

	List<NamedEntity> find(EType etype);

	void deleteEntity(NamedEntity e);

}
