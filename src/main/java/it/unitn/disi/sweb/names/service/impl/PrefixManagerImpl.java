package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.Prefix;
import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.repository.PrefixDAO;
import it.unitn.disi.sweb.names.repository.UsageStatisticsDAO;
import it.unitn.disi.sweb.names.service.PrefixManager;
import it.unitn.disi.sweb.names.utils.Pair;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("prefixManager")
public class PrefixManagerImpl implements PrefixManager {

	private PrefixDAO prefixDao;
	private UsageStatisticsDAO statDao;

	private static final boolean NORMALIZE = true;

	@Override
	public void updatePrefixes() {
		List<UsageStatistic> all = this.statDao.findAll();

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
		Prefix p = this.prefixDao.findByPrefixSelected(prefix, name);
		if (p == null) {
			p = new Prefix();
			p.setFrequency(frequency);
			p.setPrefix(prefix);
			p.setSelected(name);
			this.prefixDao.save(p);
		} else if (p.getFrequency() < frequency) {
			p.setFrequency(frequency);
			this.prefixDao.update(p);
		}

	}

	private String[] computePrefixes(String query) {
		if (query == null || query.length() == 0) {
			return null;
		}

		String[] prefixes = new String[query.length()];
		for (int i = 0; i < query.length(); i++) {
			prefixes[i] = query.substring(0, i);
		}

		return prefixes;
	}

	@Override
	public List<Pair<FullName, Double>> search(String prefix) {
		prefix = NORMALIZE ? normalize(prefix) : prefix;
		List<Prefix> list = this.prefixDao.findByPrefix(prefix);
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
		prefix = prefix.toLowerCase();
		if (!Normalizer.isNormalized(prefix, Normalizer.Form.NFKD)) {
			String normalized = Normalizer.normalize(prefix,
					Normalizer.Form.NFKD);
			normalized = normalized.replaceAll(
					"\\p{InCombiningDiacriticalMarks}+", "");
			return normalized;
		} else {
			return prefix;
		}
	}

	@Override
	public List<Pair<FullName, Double>> search(String prefix, EType etype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Autowired
	public void setPrefixDao(PrefixDAO prefixDao) {
		this.prefixDao = prefixDao;
	}

	@Autowired
	public void setStatDao(UsageStatisticsDAO statDao) {
		this.statDao = statDao;
	}

	private class PrefixComparator implements Comparator<Prefix> {
		@Override
		public int compare(Prefix o1, Prefix o2) {
			return Double.compare(o1.getFrequency(), o2.getFrequency());
		}

	}
}
