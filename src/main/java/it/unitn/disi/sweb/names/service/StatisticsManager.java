package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.FullName;

import java.util.Map;

public interface StatisticsManager {

	void updateSearchStatistic(String query, FullName selected);

	void updateMatchStatistic(String source, String target, double similarity);

	/**
	 * this function search in the database for the entries in the usage
	 * statistic table for "query". It returns the list of most frequently
	 * selected names (when the specified "query" is searched). The parameter
	 * maxNrResult specifies the max number of results that will be returned
	 *
	 * @param query
	 * @param maxNrResults
	 * @return list of most frequently selected names for the input query
	 */
	Map<FullName, Double> retrieveTopResults(String query, int maxNrResults);
}
