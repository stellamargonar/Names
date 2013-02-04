package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.Translation;

import java.util.List;

/**
 * DictionaryDao is the interface for CRUD operation translations of entity
 * names present in the database. The transaltions are retrieved from Heiner
 * Database
 *
 * @author stella margonar #LINKTESI# #LINKTESIENRICO#
 *
 */
public interface DictionaryDAO {

	/**
	 * stores a new translation
	 *
	 * @param created
	 *            translation object to be persisted
	 * @return the translation stored in the database
	 */
	Translation create(Translation created);

	/**
	 * retrieves the Translation object identified by the given id if any,
	 * returns null otherwise
	 *
	 * @param id
	 *            translation identifier
	 * @return corresponding translation in the databse
	 */
	Translation getById(int id);

	/**
	 * retrieves the list of strings which are exact translation of the names
	 * passed in input
	 *
	 * @param name
	 *            name to be searched
	 * @return list of stored translations
	 */
	List<String> findTranslations(String name);

	/**
	 * check in the database whether the first parameter is an exact translation
	 * of the second one
	 *
	 * @param source
	 *            first name
	 * @param target
	 *            second name
	 * @return true iff database contains (string,target) or (target,string),
	 *         false otherwise
	 */
	boolean contains(String source, String target);
}