package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.Prefix;
import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.repository.PrefixDAO;
import it.unitn.disi.sweb.names.repository.UsageStatisticsDAO;
import it.unitn.disi.sweb.names.service.PrefixManager;
import it.unitn.disi.sweb.names.utils.Pair;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("prefixManager")
public class PrefixManagerImpl implements PrefixManager {

	private PrefixDAO prefixDao;
	private UsageStatisticsDAO statDao;

	private static boolean NORMALIZE;

	@Override
	public void updatePrefixes() {
		List<UsageStatistic> all = statDao.findAll();

		// TODO add prefix for each name token
		// TODO check prefix empty
		for (UsageStatistic u : all) {
			String[] prefixes = computePrefixes(u.getQuery());
			for (String p : prefixes) {
				p = NORMALIZE ? normalize(p) : p;
				update(p, u.getSelected(), u.getFrequency());
			}
		}

	}

	private void update(String prefix, FullName name, double frequency) {
		Prefix p = prefixDao.findByPrefixSelected(prefix, name);
		if (p == null) {
			p = new Prefix();
			p.setFrequency(frequency);
			p.setPrefix(prefix);
			p.setSelected(name);
			prefixDao.save(p);
		} else if (p.getFrequency() < frequency) {
			p.setFrequency(frequency);
			prefixDao.update(p);
		}

	}

	private String[] computePrefixes(String query) {
		if (query == null || query.length() == 0) {
			return null;
		}
		String queryNormalized = StringCompareUtils.normalize(query);
		String[] prefixes = new String[queryNormalized.length()];
		for (int i = 0; i < queryNormalized.length(); i++) {
			prefixes[i] = queryNormalized.substring(0, i);
		}

		return prefixes;
	}

	@Override
	public List<Pair<FullName, Double>> search(String prefix) {
		String prefixNormalized = NORMALIZE ? normalize(prefix) : prefix;
		List<Prefix> list = prefixDao.findByPrefix(prefixNormalized);
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}

		List<Pair<FullName, Double>> result = new ArrayList<>(list.size());
		Collections.sort(list, new PrefixComparator());
		for (Prefix p : list) {
			result.add(new Pair<FullName, Double>(p.getSelected(), p
					.getFrequency()));
		}
		return result;
	}

	@Override
	public String normalize(String prefix) {
		return StringCompareUtils.normalize(prefix);
	}

	@Override
	public List<Pair<FullName, Double>> search(String prefix, EType etype) {
		// TODO improve implementation using directly a query
		List<Pair<FullName, Double>> all = search(prefix);
		List<Pair<FullName, Double>> result = new ArrayList<>(all.size());
		for (Pair<FullName, Double> p : all) {
			FullName f = p.key;
			if (f.getEntity().getEType().equals(etype)) {
				result.add(p);
			}
		}
		return result;
	}

	@Autowired
	public void setPrefixDao(PrefixDAO prefixDao) {
		this.prefixDao = prefixDao;
	}

	@Autowired
	public void setStatDao(UsageStatisticsDAO statDao) {
		this.statDao = statDao;
	}

	@Value("${prefix.normalize}")
	public void setNormalize(boolean n) {
		NORMALIZE = n;
	}

	private class PrefixComparator implements Comparator<Prefix> {
		@Override
		public int compare(Prefix o1, Prefix o2) {
			return Double.compare(o1.getFrequency(), o2.getFrequency());
		}

	}
}
