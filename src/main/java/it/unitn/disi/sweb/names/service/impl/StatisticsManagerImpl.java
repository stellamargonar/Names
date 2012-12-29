package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.repository.UsageStatisticsDAO;
import it.unitn.disi.sweb.names.service.StatisticsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("statisticsManager")
public class StatisticsManagerImpl implements StatisticsManager {

	private UsageStatisticsDAO statDao;

	@Override
	public void updateSearchStatistic(String query, FullName selected) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMatchStatistic(String source, String target,
			double similarity) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<FullName> retrieveTopResults(String query, int maxNrResults) {
		List<UsageStatistic> list = this.statDao.findByQuery(query);
		List<FullName> result = new ArrayList<>(maxNrResults);

		// sort list based on the frequency of the selected result for that
		// specific query
		Collections.sort(list, new Comparator<UsageStatistic>() {
			@Override
			public int compare(UsageStatistic o1, UsageStatistic o2) {
				return Double.compare(o1.getFrequency(), o2.getFrequency());
			}
		});

		for (int i = 0; i < list.size() && i < maxNrResults; i++) {
			result.add(list.get(i).getSelected());
		}
		return result;
	}


	@Autowired
	public void setStatDao(UsageStatisticsDAO statDao) {
		this.statDao = statDao;
	}
}
