package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;

public interface NameMatch {

	/**
	 * computes the similarity of the names based on their string
	 * representation. Addresses misspellings variations using edit distance
	 * based algorithm (Jaro Winkler, Levensthein)
	 *
	 * @param name1
	 *            name source
	 * @param name2
	 *            name target
	 * @param eType
	 *            common etype
	 * @return similarity value, between 0 and 1
	 */
	double stringSimilarity(String name1, String name2, EType eType);

	/**
	 * computes the similarity of the names based on their string
	 * representation. Addresses misspellings variations using edit distance
	 * based algorithm (Jaro Winkler, Levensthein).
	 *
	 * Takes as input object of type FullName, allowing a more accurate
	 * comparison for the names. (Uses "nameToCompare" instead of the entire
	 * name string)
	 *
	 * @param name1
	 *            name source
	 * @param name2
	 *            name target
	 * @param eType
	 *            common etype
	 *
	 * @return similarity value, between 0 and 1
	 */
	double stringSimilarity(FullName name1, FullName name2, EType eType);

	/**
	 * Search inside the database if the two names are alternative
	 * representation of the same entity. uses also misspellings comparator.
	 *
	 * @param name1
	 *            name source
	 * @param name2
	 *            name target
	 * @param eType
	 *            common etype
	 *
	 * @return similarity value, between 0 and 1
	 */
	double dictionaryLookup(String name1, String name2, EType eType);

	/**
	 * Compare the names using the tokens that compose them. Searches inside the
	 * database for instances of the same name already tokenized for more
	 * accurate comparison
	 *
	 * @param name1
	 *            name source
	 * @param name2
	 *            name target
	 * @param eType
	 *            common etype
	 *
	 * @return similarity value, between 0 and 1
	 */
	double tokenAnalysis(String name1, String name2, EType eType);

}
