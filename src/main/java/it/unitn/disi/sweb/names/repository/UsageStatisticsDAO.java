package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.UsageStatistic;

import java.util.List;

public interface UsageStatisticsDAO {

	UsageStatistic save(UsageStatistic stat);

	UsageStatistic update(UsageStatistic stat);

	void delete(UsageStatistic stat);

	UsageStatistic findById(int id);

	List<UsageStatistic> findByQuery(String query);

	UsageStatistic findByQuerySelected(String query, FullName selected);

	List<UsageStatistic> findAll();
}
