package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.repository.FullNameDAO;
import it.unitn.disi.sweb.names.repository.IndividualNameDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.NameTokenDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;
import it.unitn.disi.sweb.names.service.NameManager;
import it.unitn.disi.sweb.names.service.SearchType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("nameManager")
public class NameManagerImpl implements NameManager {

	private FullNameDAO fullnameDao;
	private IndividualNameDAO nameDao;
	private TriggerWordDAO twDao;
	private NameElementDAO nameElementDao;
	private NameTokenDAO nameTokenDao;

	private final static int NGRAM_DIFFERENCE = 70;

	@Override
	public FullName createFullName(String name, NamedEntity en) {
		if (en == null || name == null || name.equals("")) {
			return null;
		}

		List<FullName> foundList = this.fullnameDao.findByNameToCompare(name);

		for (FullName f : foundList) {
			if (en.equals(f.getEntity())) {
				return f;
			}
		}
		FullName fullname = new FullName();
		fullname.setName(name);
		fullname.setEntity(en);

		fullname = parse(fullname, en.getEType());
		fullname.setNameToCompare(getNameToCompare(fullname));
		fullname.setNameNormalized(getNameNormalized(fullname));
		fullname.setnGramCode(computeNGram(name));
		FullName returned = this.fullnameDao.update(fullname);

		return returned;
	}

	/**
	 * generate the nameToCompare field for the FullName. The string is compute
	 * deleting the tokens (some trigger words) that should not be considered in
	 * the comparison (see TriggerWordType)
	 *
	 * @param fullname
	 * @return
	 */
	private String getNameToCompare(FullName fullname) {
		return getNameModified(fullname, false, true);
	}

	/**
	 * generate the normalized field for the FullName. In this implementation
	 * the normalized name cosists in the tokens in lexical graphic order
	 *
	 * @param fullname
	 * @return
	 */
	private String getNameNormalized(FullName fullname) {
		return getNameModified(fullname, true, false);
	}

	private String getNameModified(FullName fullname, boolean normalized,
			boolean compare) {
		String name = "";
		Collection a = fullname.getNameTokens();
		Collection b = fullname.getTriggerWordTokens();
		int max = 0;
		if (a != null) {
			max = a.size();
			if (b != null) {
				max += b.size();
			}
		}
		String[] tokenArray = new String[max];
		for (NameToken n : fullname.getNameTokens()) {
			tokenArray[n.getPosition()] = n.getIndividualName().getName();
		}
		if (fullname.getTriggerWordTokens() != null) {
			for (TriggerWordToken t : fullname.getTriggerWordTokens()) {
				if (compare && t.getTriggerWord().getType().isComparable()
						|| !compare) {
					tokenArray[t.getPosition()] = t.getTriggerWord()
							.getTriggerWord();
				}
			}
		}

		List<String> tokens = Arrays.asList(tokenArray);

		if (normalized) {
			Collections.sort(tokens);
		}

		for (String s : tokens) {
			if (s != null) {
				name += s + " ";
			}
		}

		return name;
	}

	protected FullName parse(FullName fullname, EType eType) {
		String name = fullname.getName();

		String[] tokens = name.split(" ");
		int position = 0;
		for (String s : tokens) {
			if (!s.equals(" ")) {
				// check if it is a known name
				List<IndividualName> listName = this.nameDao.findByNameEtype(s,
						eType);
				if (listName != null && listName.size() > 0) {
					NameToken nt = new NameToken();
					nt.setFullName(fullname);
					nt.setIndividualName(listName.get(0));
					nt.setPosition(position);
					fullname.addNameToken(nt);
				} else {
					// check if it is a known triggerword
					List<TriggerWord> listTW = this.twDao
							.findByTriggerWordEtype(s, eType);
					if (listTW != null && listTW.size() > 0) {
						TriggerWordToken twt = new TriggerWordToken();
						twt.setFullName(fullname);
						twt.setPosition(position);
						twt.setTriggerWord(listTW.get(0));
						fullname.addTriggerWordToken(twt);
					} else {
						// add not existing token
						NameToken nt = new NameToken();
						nt.setIndividualName(addNewIndividualName(s, eType,
								position));
						nt.setFullName(fullname);
						nt.setPosition(position);
						fullname.addNameToken(nt);
					}
				}
				position++;
			}
		}

		return fullname;
	}

	private IndividualName addNewIndividualName(String s, EType eType,
			int position) {
		IndividualName name = new IndividualName();
		name.setName(s);
		name.setNameElement(getNewNameElement(eType, position));
		this.nameDao.save(name);
		return name;
	}

	private NameElement getNewNameElement(EType eType, int position) {
		switch (eType.getEtype()) {
			case "Location" :
			case "Organization" :
				return this.nameElementDao.findByNameEType("ProperNoun", eType);
			case "Person" :
				switch (position) {
					case 0 :
						return this.nameElementDao.findByNameEType("GivenName",
								eType);
					case 2 :
						return this.nameElementDao.findByNameEType(
								"MiddleName", eType);
					default :
						return this.nameElementDao.findByNameEType(
								"FamilyName", eType);
				}
			default :
				return null;
		}
	}

	@Override
	public void createIndividualName(String string, NameElement el) {
		IndividualName name = new IndividualName();
		name.setName(string);
		name.setNameElement(el);
		this.nameDao.save(name);

		NameToken nt = new NameToken();
		nt.setFullName(this.fullnameDao.findById(1650));
		nt.setIndividualName(name);
		nt.setPosition(0);
		this.nameTokenDao.save(nt);

	}

	@Override
	@Transactional
	public List<FullName> retrieveVariants(String name, EType etype) {
		return this.fullnameDao.findVariant(name, etype);
	}

	@Override
	@Transactional
	public FullName find(int id) {
		return this.fullnameDao.findById(id);
	}

	@Override
	@Transactional
	public List<FullName> find(String name, EType etype) {
		return this.fullnameDao.findByNameEtype(name, etype);
	}

	@Override
	@Transactional
	public List<FullName> find(String name) {
		return this.fullnameDao.findByName(name);
	}

	@Override
	public List<FullName> find(NamedEntity entity) {
		return this.fullnameDao.findByEntity(entity);
	}

	@Override
	public List<FullName> find(String name, SearchType type) {
		switch (type) {
			case NORMALIZED :
				return this.fullnameDao.findByNameNormalized(name);
			case TOCOMPARE :
				return this.fullnameDao.findByNameToCompare(name);
			case SINGLETOKEN :
				return this.fullnameDao.findByToken(name);
			case NGRAM :
				return this.fullnameDao.findByNgram(computeNGram(name),
						computeMaxDifference(name));
			default :
				break;
		}
		return null;
	}

	@Override
	public int computeNGram(String name) {
		String[] grams = genereteGrams(name, 3);
		List<Integer> sums = new ArrayList<Integer>(grams.length);
		for (String g : grams) {
			for (char c : g.toCharArray()) {
				sums.add(c + 0);
			}
		}

		return sumAll(sums); // return concatenateAll(sums);
	}

	private int computeMaxDifference(String name) {
		return name.length() / 3 + 1 * NGRAM_DIFFERENCE;
	}

	private int sumAll(List<Integer> sums) {
		int sum = 0;
		for (Integer i : sums) {
			sum += i;
		}

		return sum;
	}

	private String[] genereteGrams(String name, int size) {
		if (name == null || name.length() == 0) {
			return null;
		}

		// case of name shorter than gram size
		if (name.length() <= size) {
			String[] result = new String[1];
			result[0] = name;
			return result;
		}
		String[] grams = new String[name.length() - size];
		for (int i = 0; i + size < name.length(); i++) {
			grams[i] = name.substring(i, i + size);
		}
		return grams;
	}

	@Autowired
	public void setFullnameDao(FullNameDAO fullnameDao) {
		this.fullnameDao = fullnameDao;
	}

	@Autowired
	public void setNameDao(IndividualNameDAO nameDao) {
		this.nameDao = nameDao;
	}

	@Autowired
	public void setTwDao(TriggerWordDAO twDao) {
		this.twDao = twDao;
	}

	@Autowired
	public void setNameElementDao(NameElementDAO nameElementDao) {
		this.nameElementDao = nameElementDao;
	}

	@Autowired
	public void setNameTokenDao(NameTokenDAO nameTokenDao) {
		this.nameTokenDao = nameTokenDao;
	}

}
