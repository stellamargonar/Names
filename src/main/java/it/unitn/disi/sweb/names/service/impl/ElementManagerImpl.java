package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordStatistic;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.IndividualNameDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.NameTokenDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordTokenDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordTypeDAO;
import it.unitn.disi.sweb.names.service.ElementManager;
import it.unitn.disi.sweb.names.service.NameMatch;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("elementManager")
public class ElementManagerImpl implements ElementManager {

	private NameElementDAO nameDao;
	private TriggerWordTypeDAO twDao;
	private IndividualNameDAO nDao;
	private TriggerWordDAO tDao;
	private NameTokenDAO nameTokenDao;
	private TriggerWordTokenDAO twTokenDao;
	private NameMatch nameMatch;

	@Autowired
	public void setNameMatch(NameMatch nameMatch) {
		this.nameMatch = nameMatch;
	}
	@Autowired
	public void setNameDao(NameElementDAO nameDao) {
		this.nameDao = nameDao;
	}
	@Autowired
	public void setTwDao(TriggerWordTypeDAO twDao) {
		this.twDao = twDao;
	}
	@Autowired
	public void setnDao(IndividualNameDAO nDao) {
		this.nDao = nDao;
	}
	@Autowired
	public void settDao(TriggerWordDAO tDao) {
		this.tDao = tDao;
	}
	@Autowired
	public void setNameTokenDao(NameTokenDAO nameTokenDao) {
		this.nameTokenDao = nameTokenDao;
	}
	@Autowired
	public void setTwTokenDao(TriggerWordTokenDAO twTokenDao) {
		this.twTokenDao = twTokenDao;
	}

	@Override
	public List<NameElement> findNameElement(EType etype) {
		return nameDao.findByEType(etype);
	}

	@Override
	public List<TriggerWordType> findTriggerWordType(EType etype) {
		return twDao.findByEType(etype);
	}

	@Override
	public NameElement findNameElement(String element, EType etype) {
		return nameDao.findByNameEType(element.toLowerCase(), etype);
	}

	@Override
	public TriggerWordType findTriggerWordType(String type, EType etype) {
		return twDao.findByNameEType(type.toLowerCase(), etype);
	}

	@Override
	public List<Object> findMisspellings(String s) {
		List<IndividualName> names = nDao.findByNGram(
				StringCompareUtils.computeNGram(s),
				StringCompareUtils.computeMaxDifference(s));
		List<TriggerWord> words = tDao.findByNGram(
				StringCompareUtils.computeNGram(s),
				StringCompareUtils.computeMaxDifference(s));

		List<Object> result = new ArrayList<>();
		if (names != null) {
			for (IndividualName i : names) {
				if (nameMatch.stringSimilarity(s, i.getName(), null) > 0) {
					result.add(i);
				}
			}
		}
		if (words != null) {
			for (TriggerWord t : words) {
				if (nameMatch.stringSimilarity(s, t.getTriggerWord(), null) > 0) {
					result.add(t);
				}
			}
		}

		// Sort by frequency
		Collections.sort(result, new FrequencyComparator());

		return result;
	}

	@Override
	public List<FullName> find(IndividualName name) {
		List<NameToken> tokens = nameTokenDao.findByIndividualName(name);
		Set<FullName> result = new HashSet<>();

		for (NameToken n : tokens) {
			result.add(n.getFullName());
		}

		return new ArrayList<>(result);
	}

	@Override
	public List<FullName> find(TriggerWord triggerWord) {
		List<TriggerWordToken> tokens = twTokenDao
				.findByTriggerWord(triggerWord);
		Set<FullName> result = new HashSet<>();

		for (TriggerWordToken t : tokens) {
			result.add(t.getFullName());
		}

		return new ArrayList<>(result);
	}

	@Override
	public List<TriggerWord> findTriggerWord(String t) {
		return tDao.findByTriggerWord(t.toLowerCase());
	}

	@Override
	public TriggerWord createTriggerWord(String triggerWord,
			TriggerWordType type) {
		TriggerWord t = new TriggerWord();
		t.setTriggerWord(triggerWord);
		t.setType(type);
		t.setnGramCode(StringCompareUtils.computeNGram(triggerWord));
		return tDao.save(t);
	}

	@Override
	public int frequency(String token, NameElement el) {
		IndividualName i = nDao.findByNameElement(token.toLowerCase(), el);
		if (i != null) {
			return i.getFrequency();
		} else {
			return 0;
		}
	}

	private class FrequencyComparator implements Comparator<Object> {

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 == null) {
				if (o2 == null) {
					return 0;
				} else {
					return -1;
				}
			} else {
				if (o2 == null) {
					return 1;
				}

				// both not null
				int f1 = retrieveFrequency(o1);
				int f2 = retrieveFrequency(o2);
				return f1 - f2;
			}
		}

		private int retrieveFrequency(Object o) {
			int f = 0;
			if (o instanceof IndividualName) {
				f = ((IndividualName) o).getFrequency();
			} else if (o instanceof TriggerWord) {
				Set<TriggerWordStatistic> stat = ((TriggerWord) o)
						.geteTypeStats();

				// TODO does not work
				// if (stat != null) {
				// for (TriggerWordStatistic s : stat) {
				// f += s.getFrequency();
				// }
				// }
			}
			return f;
		}
	}

	@Override
	public TriggerWord findTriggerWord(String t, TriggerWordType type) {
		return tDao.findByTriggerWordType(t.toLowerCase(), type);
	}
}
