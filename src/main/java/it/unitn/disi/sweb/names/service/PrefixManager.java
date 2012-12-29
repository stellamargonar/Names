package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;

import java.util.List;

public interface PrefixManager {

	/**
	 * based on the usageStatistic table, generates the prefixes entities and
	 * store them in table. used for updating una tantum
	 */
	void updatePrefixes();

	/**
	 * search in the database for all the entries corresponding to the input
	 * prefix.
	 *
	 * The returned list is ordered by frequency
	 *
	 * @param prefix
	 * @return list of names corresponding to input prefix
	 */
	List<FullName> search(String prefix);

	/**
	 * search in the database for all the entries corresponding to the input
	 * prefix, which has the same etype as the one in input
	 *
	 * The returned list is ordered by frequency
	 *
	 * @param prefix
	 * @param etype
	 * @return list of name with etype in input, and corresponding to the prefix
	 */
	List<FullName> search(String prefix, EType etype);

}
