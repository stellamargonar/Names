package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;
import java.util.Map.Entry;

public interface NameManager {
	/**
	 * retrieved the full name for the entity in input if there already exists a
	 * name corresponding to the input stirng (fot this particulare entity) or
	 * creates a new FullName object parsing the input string.
	 *
	 * Stores the name in the database.
	 *
	 * @param name
	 *            string representing the complete name
	 * @param en
	 *            entity
	 * @return full name object representing the stirng
	 */
	FullName createFullName(String name, NamedEntity en);
	FullName createFullName(String name, List<Entry<String, Object>> tokens,
			NamedEntity en);

	List<Entry<String, Object>> parseFullName(String name, EType en);

	List<FullName> retrieveVariants(String name, EType etype);

	// FIND METHODS

	FullName find(int id);

	List<FullName> find(String name, EType etype);

	List<FullName> find(String name);

	List<FullName> find(NamedEntity entity);

	List<FullName> find(String name, SearchType type);

	boolean translatable(FullName f);

}
