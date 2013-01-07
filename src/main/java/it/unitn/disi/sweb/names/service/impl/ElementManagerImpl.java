package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.IndividualNameDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.NameTokenDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordTokenDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordTypeDAO;
import it.unitn.disi.sweb.names.service.ElementManager;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.ArrayList;
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
		return nameDao.findByNameEType(element, etype);
	}

	@Override
	public TriggerWordType findTriggerWordType(String type, EType etype) {
		return twDao.findByNameEType(type, etype);
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
			result.addAll(names);
		}
		if (words != null) {
			result.addAll(words);
		}
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
		return tDao.findByTriggerWord(t);
	}

}
