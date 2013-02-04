package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;

/**
 * interface for computing the match between two names. The main method for
 * computing the overall match for two names is
 * {@link #match(String, String, EType) match} method and
 * {@link #match(FullName, FullName) match}
 *
 * This main method calls 3 specific match function for comparing with different
 * strategies the two names:
 * <ul>
 * <li>{@link #stringSimilarity(String, String, EType) stringSimilarity} and the
 * respective {@link #stringSimilarity(FullName, FullName, EType)
 * stringSimilarity} compare the two names considering only their graphic
 * representation</li>
 * <li>{@link #dictionaryLookup(String, String, EType) dictionaryLookup}
 * {@link #dictionaryLookup(FullName, FullName) dictionaryLookup} check wheter
 * names are variant of each other (considering also misspellings)</li>
 * <li>{@link #tokenAnalysis(String, String, EType) tokenAnalysis}
 * {@link #tokenAnalysis(FullName, FullName) tokenAnalysis} compare the tokens
 * in the two names a pair at the time</li>
 * </ul>
 *
 * @author stella margonar #LINKTESI# #LINKTESIENRICO#
 *
 */
public interface NameMatch {

	/**
	 * combines all the listed matching functions
	 *
	 * @param name1
	 * @param name2
	 * @param etype
	 * @return
	 */
	double match(String name1, String name2, EType etype);
	double match(FullName name1, FullName name2);

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

	double dictionaryLookup(FullName name1, FullName name2);

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

	double tokenAnalysis(FullName name1, FullName name2);

}
