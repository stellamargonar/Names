package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.FullName;

import java.util.Map;

/**
 * Interface that manages the user statistics for the system.
 *
 * The methods exposed in this interface should be called by the external
 * program which uses this framework after each search or match, for updating
 * the user preferences. The methods that should be called from the outside are:
 * <ul>
 * <li>{@link #updateSearchStatistic(String, FullName) updateSearchStatistic}</li>
 * <li>{@link #updateMatchStatistic(String, String, double)
 * updateMatchStatistic}</li>
 * </ul>
 *
 * The Statistic Manager also provides methods for analysing the statistics
 * stored and improve using them the speed and accuracy of the algorithms
 *
 * @author stella margonar #LINKTESI# #LINKTESIENRICO#
 *
 */
public interface StatisticsManager {

	/**
	 *
	 * @param query
	 * @param selected
	 */
	void updateSearchStatistic(String query, FullName selected);

	/**
	 *
	 * @param source
	 * @param target
	 * @param similarity
	 */
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

	/**
	 *
	 * @param query
	 * @param selected
	 * @return
	 */
	double retrieveFrequency(String query, FullName selected);
}
