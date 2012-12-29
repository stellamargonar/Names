package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.Prefix;
import it.unitn.disi.sweb.names.repository.PrefixDAO;
import it.unitn.disi.sweb.names.service.PrefixManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("prefixManager")
public class PrefixManagerImpl implements PrefixManager {

	private PrefixDAO prefixDao;

	@Override
	public void updatePrefixes() {
		this.prefixDao.save(null);
		// TODO Auto-generated method stub

	}
	@Override
	public List<FullName> search(String prefix) {
		List<Prefix> list = this.prefixDao.findByPrefix(prefix);
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}

		List<FullName> result = new ArrayList<>(list.size());
		Collections.sort(list, new PrefixComparator());
		for (Prefix p : list) {
			result.add(p.getSelected());
		}
		return result;
	}
	@Override
	public List<FullName> search(String prefix, EType etype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Autowired
	public void setPrefixDao(PrefixDAO prefixDao) {
		this.prefixDao = prefixDao;
	}

	private class PrefixComparator implements Comparator<Prefix> {
		@Override
		public int compare(Prefix o1, Prefix o2) {
			return Double.compare(o1.getFrequency(), o2.getFrequency());
		}

	}
}
