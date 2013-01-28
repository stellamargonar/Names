package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.FullNameDAO;
import it.unitn.disi.sweb.names.repository.IndividualNameDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;
import it.unitn.disi.sweb.names.service.ElementManager;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.EtypeName;
import it.unitn.disi.sweb.names.service.NameManager;
import it.unitn.disi.sweb.names.service.SearchType;
import it.unitn.disi.sweb.names.service.TranslationManager;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("nameManager")
public class NameManagerImpl implements NameManager {

	private FullNameDAO fullnameDao;
	private IndividualNameDAO nameDao;
	private TriggerWordDAO twDao;
	private NameElementDAO nameElementDao;
	private TranslationManager translationManager;

	@Autowired
	EntityManager entityManager;
	@Autowired
	ElementManager elementManager;

	@Override
	public FullName createFullName(String name,
			List<Entry<String, Object>> tokens, NamedEntity en) {
		if (en == null || tokens == null || name == null || name.isEmpty()
				|| tokens.isEmpty()) {
			return null;
		}

		List<FullName> foundList = find(name, SearchType.TOCOMPARE);

		// if the name is already saved in the database, return the stored
		// instance
		for (FullName f : foundList) {
			if (en.equals(f.getEntity())) {
				return f;
			}
		}

		FullName fullname = new FullName();
		fullname.setName(name);
		fullname.setEntity(en);

		fullname = buildFullName(fullname, tokens);

		fullname.setNameToCompare(getNameToCompare(fullname));
		fullname.setNameNormalized(getNameNormalized(fullname));
		fullname.setnGramCode(StringCompareUtils.computeNGram(name));
		FullName returned = fullnameDao.update(fullname);

		return returned;
	}

	@Override
	public FullName createFullName(String name, NamedEntity en) {
		// TODO edit with new methods
		// call parseFullName
		// and then create fullname(list tokens)

		if (en == null || name == null || name.equals("")) {
			return null;
		}

		List<Entry<String, Object>> tokens = parseFullName(name, en.getEType());
		return createFullName(name, tokens, en);
		//
		// List<FullName> foundList = find(name, SearchType.TOCOMPARE);
		//
		// for (FullName f : foundList) {
		// if (en.equals(f.getEntity())) {
		// return f;
		// }
		// }
		// FullName fullname = new FullName();
		// fullname.setName(name);
		// fullname.setEntity(en);
		//
		// fullname = parse(fullname, en.getEType());
		//
		// fullname.setNameToCompare(getNameToCompare(fullname));
		// fullname.setNameNormalized(getNameNormalized(fullname));
		// fullname.setnGramCode(StringCompareUtils.computeNGram(name));
		// FullName returned = fullnameDao.update(fullname);
		//
		// return returned;
	}

	@Override
	public List<Entry<String, Object>> parseFullName(String name, EType e) {
		List<Entry<String, Object>> result = new ArrayList<>();

		// Tokenize
		String[] tokens = StringCompareUtils.generateTokens(name);

		// for each token
		for (String tok : tokens) {
			Entry<String, Object> token = new AbstractMap.SimpleEntry(tok, null);

			// is Individual Name?
			Entry<NameElement, Double> nameElement = chooseNameElement(tok, e);

			// is trigger word?
			Entry<TriggerWordType, Double> triggerElement = chooseTriggerWordElement(
					tok, e);

			// choose one with highest probability
			if (nameElement.getValue() >= triggerElement.getValue()) {
				token.setValue(nameElement.getKey());
			} else {
				token.setValue(triggerElement.getKey());
			}

			// add token to list
			result.add(token);
		}
		return result;
	}

	/**
	 * based on heuristics, finds the most probable triggerwork element that
	 * corresponds to the given string and return it with a measure of
	 * confidence
	 *
	 * @param tok
	 * @param e
	 * @return
	 */
	private Entry<TriggerWordType, Double> chooseTriggerWordElement(String tok,
			EType etype) {
		// TODO Auto-generated method stub
		// TODO add heristics and statistics
		List<TriggerWord> listTW = twDao.findByTriggerWordEtype(tok, etype);
		if (listTW != null && listTW.size() > 0) {
			return new AbstractMap.SimpleEntry(listTW.get(0), 1.0);
		}
		elementManager.findTriggerWordType(etype).get(0);
		// return new AbstractMap.SimpleEntry( ,1.0);
		return new AbstractMap.SimpleEntry(elementManager.findTriggerWordType(
				etype).get(0), 1.0);
	}

	/**
	 * based on heuristics, finds the most probable name element that
	 * corresponds to the given string and return it with a measure of
	 * confidence
	 *
	 * @param tok
	 * @param etype
	 * @return
	 */
	private Entry<NameElement, Double> chooseNameElement(String tok, EType etype) {
		Entry<NameElement, Double> result = null;
		int max = 0;
		// for each namelement in etype
		for (NameElement el : elementManager.findNameElement(etype)) {
			int freq = elementManager.frequency(tok, el);
			if (freq > max) {
				max = freq;
				result = new AbstractMap.SimpleEntry<NameElement, Double>(el,
						new Double(freq));
			}
		}
		if (max == 0) {
			// improve search with misspellings
			// TODO

			if (max == 0) {
				result = new AbstractMap.SimpleEntry<NameElement, Double>(
						getNewNameElement(etype, 0), 1.0);
			}
		}
		return result;
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

	/**
	 * given a list of pairs of strings and element type, this methods creates
	 * the tokens for the fullName in input. THe list must be ordered since the
	 * position of each token in the name is the position in the list.
	 *
	 * If the object passed in the pair is not an instance of NameElement of
	 * TriggerWordType the token is not considered
	 *
	 * @param name
	 * @param tokens
	 * @return FullName enriched with the input tokens
	 */
	private FullName buildFullName(FullName name,
			List<Entry<String, Object>> tokens) {
		for (int i = 0; i < tokens.size(); i++) {
			Entry<String, Object> token = tokens.get(i);

			// token is a name token
			if (token.getValue() instanceof NameElement) {
				NameToken nt = new NameToken();
				nt.setFullName(name);

				// search for the individual name in the database
				IndividualName indName = nameDao.findByNameElement(
						token.getKey(), (NameElement) token.getValue());
				// if new individual name, create a new one, otherwise add the
				// retrieved from db
				nt.setIndividualName(indName != null
						? indName
						: createIndividualName(token.getKey(),
								(NameElement) token.getValue()));
				nt.setPosition(i);
				name.addNameToken(nt);

				// token is a trigger word token
			} else if (token.getValue() instanceof TriggerWordType) {
				TriggerWordToken tk = new TriggerWordToken();
				tk.setFullName(name);
				tk.setPosition(i);

				TriggerWord t = twDao.findByTriggerWordType(token.getKey(),
						(TriggerWordType) token.getValue());
				tk.setTriggerWord(t != null ? t : elementManager
						.createTriggerWord(token.getKey(),
								(TriggerWordType) token.getValue()));
				name.addTriggerWordToken(tk);
			}
		}
		return name;
	}

	// specificare tokenizzazione euristica (con behind the name, census)
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
		// addTranslations(name);
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

	@Override
	public IndividualName createIndividualName(String name, int frequency,
			NameElement el) {
		IndividualName i = createIndividualName(name, el);
		i.setFrequency(frequency);
		nameDao.update(i);
		return i;
	}

	public IndividualName createIndividualName(String name, NameElement el) {
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
				return nameElementDao.findByNameEType(
						"GivenName".toLowerCase(), eType);
				// switch (position) {
				// case 0 :
				// return nameElementDao.findByNameEType(
				// "GivenName".toLowerCase(), eType);
				// case 2 :
				// return nameElementDao.findByNameEType(
				// "MiddleName".toLowerCase(), eType);
				// default :
				// return nameElementDao.findByNameEType(
				// "FamilyName".toLowerCase(), eType);
				// }
			default :
				return null;
		}
	}

	@Override
	@Transactional
	public List<FullName> retrieveVariants(String name, EType etype) {

		// List<NamedEntity> entities = etype != null ? entityManager.find(
		// name.toLowerCase(), etype) : entityManager.find(name
		// .toLowerCase());
		// List<FullName> result = new ArrayList<>();
		// for (NamedEntity en : entities) {
		// result.addAll(fullnameDao.findByEntity(en));
		// }

		return fullnameDao.findVariant(name.toLowerCase(), etype);
		// return result;
	}

	@Override
	@Transactional
	public FullName find(int id) {
		return fullnameDao.findById(id);
	}

	@Override
	@Transactional
	public List<FullName> find(String name, EType etype) {
		if (etype != null) {
			return fullnameDao.findByNameEtype(name.toLowerCase(), etype);
		} else {
			return find(name);
		}
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
		if (f == null || f.getEntity() == null
				|| f.getEntity().getEType() == null) {
			return false;
		}
		EType e = f.getEntity().getEType();
		EtypeName n = EtypeName.valueOf(e.getEtype().toUpperCase());

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

	public static List<Entry<String, Object>> parse(String name, EType etype) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");
		NameManager manager = context.getBean(NameManager.class);
		return manager.parseFullName(name, etype);
	}

}
