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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

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
		FullName fullname = retrieveExistingName(name, en);
		if (fullname != null) {
			return fullname;
		}

		if (tokens == null || tokens.isEmpty()) {
			return null;
		}

		fullname = buildFullName(name, tokens);
		fullname.setEntity(en);

		return fullnameDao.save(fullname);
	}

	@Override
	public FullName createFullName(String name, NamedEntity en) {
		FullName fullname = retrieveExistingName(name, en);
		if (fullname != null) {
			return fullname;
		}
		fullname = buildFullName(name, en.getEType());
		fullname.setEntity(en);
		return fullnameDao.save(fullname);
	}

	/**
	 * search in the system database if there is already a full name with the
	 * same name and entity. If this is the case, return the database object,
	 * otherwise null
	 *
	 * @param name
	 * @param en
	 * @return
	 */
	private FullName retrieveExistingName(String name, NamedEntity en) {
		if (en == null || name == null || name.isEmpty()) {
			return null;
		}

		// search for fullnames with the same name (to compare)
		List<FullName> foundList = find(name, SearchType.TOCOMPARE);

		// if the name is already saved in the database, return the stored
		// instance
		for (FullName f : foundList) {
			if (en.equals(f.getEntity())) {
				return f;
			}
		}

		// if nothing is found, return null
		return null;
	}

	@Override
	public FullName buildFullName(String name, EType e) {
		return buildFullName(name, parseFullName(name, e));
	}

	private FullName buildFullName(String name,
			List<Entry<String, Object>> tokens) {
		if (tokens == null || name == null || name.isEmpty()
				|| tokens.isEmpty()) {
			return null;
		}

		FullName fullname = new FullName();
		fullname.setName(name);

		fullname = buildFullName(fullname, tokens);

		fullname.setNameToCompare(getNameToCompare(fullname));
		fullname.setNameNormalized(getNameNormalized(fullname));
		fullname.setnGramCode(StringCompareUtils.computeNGram(name));
		return fullname;
	}

	@Override
	public List<Entry<String, Object>> parseFullName(String name, EType e) {
		List<Entry<String, Object>> result = new ArrayList<>();
		List<String> laterTokens = new ArrayList<>();
		Set<NameElement> assigned = new HashSet<>();

		// Tokenize
		String[] tokens = StringCompareUtils.generateTokens(name);

		// for each token
		for (String tok : tokens) {
			boolean assignLater = false;
			boolean chooseNameElement = false;

			Entry<String, Object> token = new AbstractMap.SimpleEntry<String, Object>(
					tok, null);

			// is Individual Name?
			Entry<NameElement, Double> nameElement = chooseNameElement(tok, e);

			// is trigger word?
			Entry<TriggerWord, Double> triggerElement = chooseTriggerWordElement(
					tok, e);

			if (nameElement == null && triggerElement == null) {
				// if both search failed, then maybe the token could be a
				// misspelled token
				List<Object> element = elementManager.findMisspellings(tok);

				// if this is the case, then add the new token with the selected
				// element type, but maintain its spelling
				if (element.size() > 0) {
					Object first = element.get(0);
					if (first instanceof IndividualName) {
						nameElement = new AbstractMap.SimpleEntry<NameElement, Double>(
								((IndividualName) first).getNameElement(), 1.0);
					} else if (first instanceof TriggerWord) {
						triggerElement = new AbstractMap.SimpleEntry<TriggerWord, Double>(
								(TriggerWord) first, 1.0);
					}
				} else {
					// add to a list of elements that will be assigned later
					laterTokens.add(tok);
					assignLater = true;
				}

			}

			// choose one with highest probability and not null
			if (!assignLater) {
				if (nameElement != null) {
					if (triggerElement != null) {
						if (nameElement.getValue() >= triggerElement.getValue()) {
							chooseNameElement = true;
						} else {
							chooseNameElement = false;
						}
					} else {
						chooseNameElement = true;
					}
				} else {
					chooseNameElement = false;
				}

				if (chooseNameElement) {
					token.setValue(nameElement.getKey());
					assigned.add(nameElement.getKey());
				} else {
					token.setValue(triggerElement.getKey());
				}
				// add token to list
				result.add(token);
			}

		}

		result.addAll(assignNameTokens(laterTokens, assigned, e));
		return result;
	}

	/**
	 * function called after {@link #parseFullName(String, EType) parseFullName}
	 * method. It assign a {@link NameElement} to tokens not yet assigned. The
	 * NameElement choice is made based in a heuristics: assign first name
	 * elements not yet assigned
	 *
	 * @param tokensToAssign
	 *            list of tokens not yet assigned
	 * @param assigned
	 *            name elements already used in the name
	 * @param etype
	 *            EType of the name
	 * @return pair of tokens, NameElement assigned
	 */
	private List<Entry<String, Object>> assignNameTokens(
			List<String> tokensToAssign, Set<NameElement> assigned, EType etype) {
		List<Entry<String, Object>> result = new ArrayList<>();

		// retrieves all name elements for the given etype
		Set<NameElement> typeElements = new HashSet<>(
				elementManager.findNameElement(etype));
		// removes already assigned NameElements
		for (NameElement element : assigned) {
			typeElements.remove(element);
		}

		Iterator<NameElement> iterator;
		// for each not assigned token
		for (String token : tokensToAssign) {
			NameElement el;
			iterator = typeElements.iterator();

			// if there are yet name element to assign, use that
			if (iterator.hasNext()) {
				el = iterator.next();
				typeElements.remove(el);
			} else {
				// otherwise find the most probable name element
				el = getNewNameElement(etype);
			}
			result.add(new AbstractMap.SimpleEntry<String, Object>(token, el));
		}
		return result;
	}

	/**
	 * based on heuristics, finds the most probable triggerword element that
	 * corresponds to the given string and return it with a measure of
	 * confidence
	 *
	 * @param tok
	 * @param e
	 * @return
	 */
	private Entry<TriggerWord, Double> chooseTriggerWordElement(String tok,
			EType etype) {

		// search for trigger words of of the given etype
		List<TriggerWord> listTW = twDao.findByTriggerWordEtype(
				tok.toLowerCase(), etype);

		// TODO order the list according to some heuristic

		// returns the first one
		if (listTW != null && listTW.size() > 0) {
			return new AbstractMap.SimpleEntry<TriggerWord, Double>(
					listTW.get(0), Double.MAX_VALUE);
		}
		return null;
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

		// TODO add recognition NUMERI ROMANI

		// for each namelement in etype
		for (NameElement el : elementManager.findNameElement(etype)) {
			// retrives its frequency of token as specific NameElement
			int freq = elementManager.frequency(tok, el);
			if (freq > max) {
				max = freq;
				result = new AbstractMap.SimpleEntry<NameElement, Double>(el,
						new Double(freq));
			}
		}
		// returns most probable NameElement
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

	/**
	 * constructs the name modified according to the input parameters. The
	 * function can construct the nameToCompare or the nameNormalized depending
	 * on the value of {@code normalized} and {@code compare}.
	 *
	 * NameToCompare is the name without trigger word which are not comparable.
	 * NameNormalized is the name with tokens ordered alphabetically
	 *
	 * @param fullname
	 * @param normalized
	 *            flag for computing the name normalized
	 * @param compare
	 *            flag for computing the name to compare
	 * @return string representing either the name to compare or name normalized
	 */
	private String getNameModified(FullName fullname, boolean normalized,
			boolean compare) {
		String name = "";
		Collection<NameToken> a = fullname.getNameTokens();
		Collection<TriggerWordToken> b = fullname.getTriggerWordTokens();

		// determine the maximum number of tokens
		int max = 0;
		if (a != null) {
			max = a.size();
			if (b != null) {
				max += b.size();
			}
		}

		// place name tokens and trigger word tokens in the proper position
		String[] tokenArray = new String[max];
		for (NameToken n : fullname.getNameTokens()) {
			tokenArray[n.getPosition()] = n.getIndividualName().getName();
		}
		if (fullname.getTriggerWordTokens() != null) {
			for (TriggerWordToken t : fullname.getTriggerWordTokens()) {

				// for name to compare, adds only trigger words comparable
				if (compare && t.getTriggerWord().getType().isComparable()
						|| !compare) {
					tokenArray[t.getPosition()] = t.getTriggerWord()
							.getTriggerWord();
				}
			}
		}

		List<String> tokens = Arrays.asList(tokenArray);

		// for name normalized, order tokens
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
		int position = 0;
		for (int i = 0; i < tokens.size(); i++) {
			Entry<String, Object> token = tokens.get(i);

			if (token.getKey() != null) {
				// token is a name token
				if (token.getValue() instanceof NameElement) {
					NameToken nt = new NameToken();
					nt.setFullName(name);

					// search for the individual name in the database
					IndividualName indName = nameDao.findByNameElement(token
							.getKey().toLowerCase(), (NameElement) token
							.getValue());
					// if new individual name, create a new one, otherwise add
					// the
					// retrieved from db
					nt.setIndividualName(indName != null
							? indName
							: createIndividualName(token.getKey(),
									(NameElement) token.getValue()));
					nt.setPosition(position);
					name.addNameToken(nt);

					// token is a trigger word token
				} else if (token.getValue() instanceof TriggerWord) {
					TriggerWordToken tk = new TriggerWordToken();
					tk.setFullName(name);

					TriggerWord t = (TriggerWord) token.getValue();
					tk.setTriggerWord(t != null ? t : elementManager
							.createTriggerWord(token.getKey(),
									(TriggerWordType) token.getValue()));
					tk.setPosition(position);
					name.addTriggerWordToken(tk);
				}
			}
			position++;

		}
		return name;
	}

	/**
	 * check whether the name in input has some known translations, and if this
	 * is the case, adds them in the proper table
	 *
	 * @param name
	 */
	protected void addTranslations(IndividualName name) {
		// retrieve translation for the name
		List<String> translationsString = translationManager
				.findTranslation(name.getName());

		NameElement el = name.getNameElement();

		// adds translation to the name
		List<IndividualName> translations = new ArrayList<>();
		for (String s : translationsString) {
			translations.add(createIndividualName(s, el));
		}
		translations.add(name);

		// stores translations
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

	/**
	 * creates and stores a new individual name
	 *
	 * @param name
	 *            string for the name
	 * @param el
	 *            NameElement corresponding
	 * @return stored instance
	 */
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

	/**
	 * returns the NameElement to assign to a token of etype in input. This
	 * function is used when a token is not recognized as individual name or
	 * trigger word already in the db, then it will be assigned to the most used
	 * NameElement depending on the EType
	 *
	 * @param eType
	 * @return name element for not assigned token
	 */
	private NameElement getNewNameElement(EType eType) {
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
			default :
				return null;
		}
	}

	@Override
	public List<FullName> retrieveVariants(String name, EType etype) {
		return fullnameDao.findVariant(name.toLowerCase(), etype);
	}

	@Override
	public FullName find(int id) {
		return fullnameDao.findById(id);
	}

	@Override
	public List<FullName> find(String name, EType etype) {
		if (etype != null) {
			return fullnameDao.findByNameEtype(name.toLowerCase(), etype);
		} else {
			return find(name);
		}
	}

	@Override
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

	/**
	 * static function for parsing a string to its tokens structure, which used
	 * {@link #parseFullName(String, EType) parseFullName} method
	 *
	 * @param name
	 *            string representing a name
	 * @param etype
	 *            etype for the name
	 * @return list of tokens with the corresponding name element or triggerword
	 */
	public static List<Entry<String, Object>> parse(String name, EType etype) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"META-INF/applicationContext.xml");
		NameManager manager = context.getBean(NameManager.class);
		return manager.parseFullName(name, etype);
	}

}
