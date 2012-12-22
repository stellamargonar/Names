package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NamedEntity;

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
	public FullName createFullName(String name, NamedEntity en) ;
	
	public void createIndividualName(String string, NameElement el);
}
