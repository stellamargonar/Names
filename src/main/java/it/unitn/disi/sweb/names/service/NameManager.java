package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;
import java.util.Map.Entry;

/**
 * Interface for business logic operation on names. Exposes methods that allows
 * to work on names without using directly the respective DAOs
 *
 * @author stella margonar #LINKTESI# #LINKTESIENRICO#
 *
 */
public interface NameManager {

	/**
	 * retrieves the full name for the entity in input if there already exists a
	 * name corresponding to the input string (for this particular entity) or
	 * creates a new FullName object parsing the input string.
	 *
	 * Stores the name in the database.
	 *
	 * @param name
	 *            string representing the complete name
	 * @param en
	 *            entity
	 * @return full name object representing the string
	 */
	FullName createFullName(String name, NamedEntity en);

	/**
	 * same as {@link #createFullName(String, NamedEntity) createFullName} but
	 * does not parse the name, because receives already as input the list of
	 * name tokens
	 *
	 * @param name
	 *            string representing the entire name
	 * @param tokens
	 *            list of tokens and NameElement/TriggerWord
	 * @param en
	 *            entity
	 * @return full name object representing the name in input
	 */
	FullName createFullName(String name, List<Entry<String, Object>> tokens,
			NamedEntity en);

	/**
	 * creates a full name object parsing the string name in input, but does not
	 * stores the result in the database. Used when we need a full name
	 * structure, for an object that should not persisted in the db.
	 *
	 * @param name
	 * @param e
	 * @return full name corresponding to the string name
	 */
	FullName buildFullName(String name, EType e);

	/**
	 * parse a string representing a name to a list of tokens. For each of them
	 * finds the most probable NameElement/TriggerWord based on heuristics and
	 * on the given EType
	 *
	 * @param name
	 *            string representing the name
	 * @param en
	 *            etype of the name
	 * @return List of token and element type
	 */
	List<Entry<String, Object>> parseFullName(String name, EType en);

	/**
	 * search in the database for variant name of the one given as input for the
	 * specified etype.
	 *
	 * Variant name are names which belong to the same entity of the one in
	 * input, but have a different identifier
	 *
	 * @param name
	 *            name for which variant are searched
	 * @param etype
	 *            etype of the entity name
	 * @return
	 */
	List<FullName> retrieveVariants(String name, EType etype);

	/**
	 * returns the FullName with identifier as input
	 *
	 * @param id
	 *            name identifier
	 * @return fullName object with identifier equals to {@code id}, null if it
	 *         does not exists
	 */
	FullName find(int id);

	List<FullName> find(String name, EType etype);

	/**
	 * search for FullName which have the same name as in input
	 *
	 * @param name
	 * @return List of FullName with {@code name} as name, null if no such
	 *         FullName exists
	 */
	List<FullName> find(String name);

	/**
	 * search for names which belong to the input entity
	 *
	 * @param entity
	 *            NamedEntity
	 * @return list of fullname, null if entity has no name, or does not exists
	 */
	List<FullName> find(NamedEntity entity);

	/**
	 * search for FullName object with criteria that depends on the {@code type}
	 * parameter.
	 * <ul>
	 * <li>if {@code type} is NORMALIZED, then search for FullName with
	 * nameToCompare equals to {@code name}</li>
	 * <li>if {@code type} is TOCOMPARE, then search for FullName with
	 * nameNormalized equals to {@code name}</li>
	 * <li>if {@code type} is SINGLETOKEN, then search for FullName which have a
	 * token equals to {@code name}</li>
	 * <li>if {@code type} is NGRAM, then search for FullName with have a ngram
	 * code that match the one of the name in input</li>
	 * </ul>
	 *
	 * @param name
	 * @param type
	 * @return
	 */
	List<FullName> find(String name, SearchType type);

	/**
	 * checks whether it makes sense for the FullName in input to be translated.
	 * For example it makes sense for the name of a city as "London" to be
	 * translated into other languages (ex: "Londra" [it]), while for a common
	 * person name like "Maria Rossi"[it] it does not make sense to translate it
	 * to "Mary Red" [en].
	 *
	 * This decision is taken based on heuristics on the trigger words contained
	 * in the name
	 *
	 * @param f
	 * @return true if the name is translatable, false otherwise
	 */
	boolean translatable(FullName f);

	/**
	 * creates and stores in the system database a new individual name with the
	 * parameter in input
	 *
	 * @param name
	 *            string representing the individual name
	 * @param frequency
	 *            number of known occurrences of this name as {@code el}
	 *            NameElement
	 * @param el
	 *            name element that the name represents
	 * @return individual name object as stored in the db
	 */
	IndividualName createIndividualName(String name, int frequency,
			NameElement el);

}
