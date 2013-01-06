package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.repository.UsageStatisticsDAO;
import it.unitn.disi.sweb.names.service.StatisticsManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("statisticsManager")
public class StatisticsManagerImpl implements StatisticsManager {

	private UsageStatisticsDAO statDao;

	@Override
	public void updateSearchStatistic(String query, FullName selected) {
		UsageStatistic old = statDao.findByQuerySelected(query, selected);
		if (old == null) {
			UsageStatistic u = new UsageStatistic();
			u.setFrequency(1);
			u.setQuery(query);
			u.setSelected(selected);
			statDao.save(u);
		} else {
			double oldFrequency = old.getFrequency();
			old.setFrequency(oldFrequency + 1);
			statDao.update(old);
		}
	}

	@Override
	public void updateMatchStatistic(String source, String target,
			double similarity) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<FullName, Double> retrieveTopResults(String query,
			int maxNrResults) {
		List<UsageStatistic> list = statDao.findByQuery(query);
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<FullName, Double> result = new HashMap<>(maxNrResults);

		// sort list based on the frequency of the selected result for that
		// specific query
		Collections.sort(list, new Comparator<UsageStatistic>() {
			@Override
			public int compare(UsageStatistic o1, UsageStatistic o2) {
				return Double.compare(o1.getFrequency(), o2.getFrequency());
			}
		});

		for (int i = 0; i < list.size() && i < maxNrResults; i++) {
			result.put(list.get(i).getSelected(), list.get(i).getFrequency());
		}
		return result;
	}

	@Autowired
	public void setStatDao(UsageStatisticsDAO statDao) {
		this.statDao = statDao;
	}

	@Override
	public double retrieveFrequency(String query, FullName selected) {
		UsageStatistic u = statDao.findByQuerySelected(query, selected);
		return u != null ? u.getFrequency() : 0;
	}
}
