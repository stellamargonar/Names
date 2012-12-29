package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.FullName;

import java.util.List;

public interface StatisticsManager {

	void updateSearchStatistic(String query, FullName selected);

	void updateMatchStatistic(String source, String target, double similarity);

	List<FullName> retrieveTopResults(String query, int maxNrResults);
}
