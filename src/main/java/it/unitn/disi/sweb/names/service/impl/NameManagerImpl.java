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
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameManager;
import it.unitn.disi.sweb.names.service.SearchType;
import it.unitn.disi.sweb.names.service.TranslationManager;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("nameManager")
public class NameManagerImpl implements NameManager {

	private FullNameDAO fullnameDao;
	private IndividualNameDAO nameDao;
	private TriggerWordDAO twDao;
	private NameElementDAO nameElementDao;
	private TranslationManager translationManager;

	@Override
	public FullName createFullName(String name, NamedEntity en) {
		if (en == null || name == null || name.equals("")) {
			return null;
		}

		List<FullName> foundList = find(name, SearchType.TOCOMPARE);

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
		fullname.setnGramCode(StringCompareUtils.computeNGram(name));
		FullName returned = fullnameDao.update(fullname);

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
		Collection<NameToken> a = fullname.getNameTokens();
		Collection<TriggerWordToken> b = fullname.getTriggerWordTokens();
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

		StringBuffer buf = new StringBuffer();
		for (String s : tokens) {
			if (s != null) {
				buf.append(s + " ");
			}
		}
		name = buf.toString();

		// removes the first or last char if they are space
		if (name.startsWith(" ")) {
			name = name.substring(1);
		}
		if (name.endsWith(" ")) {
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}

	protected FullName parse(FullName fullname, EType eType) {
		String name = fullname.getName();

		String[] tokens = StringCompareUtils.generateTokens(name);
		int position = 0;
		for (String s : tokens) {
			if (!s.equals(" ")) {
				// check if it is a known name
				List<IndividualName> listName = nameDao.findByNameEtype(
						s.toLowerCase(), eType);
				if (listName == null || listName.isEmpty()) {
					listName = nameDao.findByName(s);
				}
				if (listName != null && listName.size() > 0) {
					NameToken nt = new NameToken();
					nt.setFullName(fullname);
					nt.setIndividualName(listName.get(0));
					nt.setPosition(position);
					fullname.addNameToken(nt);
				} else {
					// check if it is a known triggerword
					List<TriggerWord> listTW = twDao.findByTriggerWordEtype(
							s.toLowerCase(), eType);
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
		IndividualName name = createIndividualName(s,
				getNewNameElement(eType, position));
		addTranslations(name);
		return name;
	}

	/**
	 * check wheter the name in input has some known translations, and if this
	 * is the case, adds them in the proper table
	 *
	 * @param name
	 */
	protected void addTranslations(IndividualName name) {
		List<String> translationsString = translationManager
				.findTranslation(name.getName());

		NameElement el = name.getNameElement();

		List<IndividualName> translations = new ArrayList<>();
		for (String s : translationsString) {
			translations.add(createIndividualName(s, el));
		}
		translations.add(name);

		for (int i = 0; i < translations.size(); i++) {
			IndividualName n = translations.get(i);
			n.addTranslations(translations);
			translations.set(i, nameDao.update(n));
		}
	}

	private IndividualName createIndividualName(String name, NameElement el) {
		IndividualName nameDb = nameDao.findByNameElement(name, el);
		if (nameDb != null) {
			return nameDb;
		}

		IndividualName i = new IndividualName();
		i.setName(name);
		i.setnGramCode(StringCompareUtils.computeNGram(name));
		i.setNameElement(el);
		return nameDao.save(i);
	}

	private NameElement getNewNameElement(EType eType, int position) {
		switch (eType.getEtype()) {
			case "Location" :
				return nameElementDao.findByNameEType(
						"ProperNoun".toLowerCase(), eType);
			case "Organization" :
				return nameElementDao.findByNameEType(
						"ProperNoun".toLowerCase(), eType);
			case "Person" :
				switch (position) {
					case 0 :
						return nameElementDao.findByNameEType(
								"GivenName".toLowerCase(), eType);
					case 2 :
						return nameElementDao.findByNameEType(
								"MiddleName".toLowerCase(), eType);
					default :
						return nameElementDao.findByNameEType(
								"FamilyName".toLowerCase(), eType);
				}
			default :
				return null;
		}
	}

	@Override
	@Transactional
	public List<FullName> retrieveVariants(String name, EType etype) {
		return fullnameDao.findVariant(name.toLowerCase(), etype);
	}

	@Override
	@Transactional
	public FullName find(int id) {
		return fullnameDao.findById(id);
	}

	@Override
	@Transactional
	public List<FullName> find(String name, EType etype) {
		return fullnameDao.findByNameEtype(name.toLowerCase(), etype);
	}

	@Override
	@Transactional
	public List<FullName> find(String name) {
		return fullnameDao.findByName(name.toLowerCase());
	}

	@Override
	public List<FullName> find(NamedEntity entity) {
		return fullnameDao.findByEntity(entity);
	}

	@Override
	public List<FullName> find(String name, SearchType type) {
		switch (type) {
			case NORMALIZED :
				return fullnameDao.findByNameNormalized(name.toLowerCase());
			case TOCOMPARE :
				return fullnameDao.findByNameToCompare(name.toLowerCase());
			case SINGLETOKEN :
				return fullnameDao.findByToken(StringCompareUtils
						.normalize(name));
			case NGRAM :
				return fullnameDao.findByNgram(
						StringCompareUtils.computeNGram(name),
						StringCompareUtils.computeMaxDifference(name));
			default :
				break;
		}
		return null;
	}

	@Override
	public boolean translatable(FullName f) {
		EType e = f.getEntity().getEType();
		EtypeName n = EtypeName.valueOf(e.getEtype());

		switch (n) {
			case PERSON :
				Set<TriggerWordToken> tokens = f.getTriggerWordTokens();
				if (tokens == null) {
					return false;
				}
				for (TriggerWordToken tok : tokens) {
					if (tok.getTriggerWord().getType().isComparable()) {
						return true;
					}
				}
				return false;

			case LOCATION :
				return true;

			case ORGANIZATION :
				// TODO
				// in case designator not translate
				// in case educational organization -> translate
				return false;
			default :
				break;
		}
		return false;
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
	public void setTranslationManager(TranslationManager translationManager) {
		this.translationManager = translationManager;
	}

}
