package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.MisspellingsComparator;
import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.IndividualNameDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;
import it.unitn.disi.sweb.names.service.ElementManager;
import it.unitn.disi.sweb.names.service.EntityManager;
import it.unitn.disi.sweb.names.service.NameManager;
import it.unitn.disi.sweb.names.service.NameMatch;
import it.unitn.disi.sweb.names.utils.Pair;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("nameMatcher")
public class NameMatchImpl implements NameMatch {

	private static final int MAX_LENGTH_DIFFERENCE = 3;
	private static final double THRESHOLD_MISSPELLINGS = 0.7;
	private static final double THRESHOLD_DICTIONARY = THRESHOLD_MISSPELLINGS;

	private IndividualNameDAO individualNameDao;
	private TriggerWordDAO triggerWordDao;

	private EntityManager entityManager;
	private NameManager nameManager;
	private ElementManager elementManager;

	private MisspellingsComparator comparator;

	private EType etype;
	public NameMatchImpl() {
	}

	@Override
	public double stringSimilarity(String name1, String name2, EType eType) {
		etype = eType;

		// TODO should we consider at this point the titles ecc, or compare only
		// the "name_to_compare" ?
		/*
		 * stringsim (obj 1, obj 2) { if (obj1 instance of string) nothing else
		 * if obj instance of some hash map with name -> field & position
		 */

		// if the strings are equals, then similarity is 1 (max value)
		if (stringEquality(name1, name2)) {
			return 1;
		} else {
			// otherwise check if they match wrt to misspellings
			return stringMatching(name1, name2);
		}
	}

	@Override
	public double stringSimilarity(FullName name1, FullName name2, EType eType) {
		// TODO not yet implemented
		return 0;
	}

	/**
	 * check wheter the two input strings are equals comparing their characters.
	 * based on the Java.lang.String.equals method
	 *
	 * @param string1
	 * @param string2
	 * @return true if they are the same string and not null, false otherwise
	 */
	private boolean stringEquality(String string1, String string2) {
		if (string1 != null && string2 != null) {
			return string1.equals(string2);
		} else {
			return false;
		}
	}

	/**
	 * compute the confidence degree for two string to match, meaning to be the
	 * same string with respect to misspellings and variations
	 *
	 * @param name1
	 *            string representing name1
	 * @param name2
	 *            string representing name2
	 * @return similarity value if they match in [0,1], or 0 if they do not
	 *         match
	 */
	private double stringMatching(String name1, String name2) {
		double sim = 0;

		// applies string comparator function only if they strings have
		// approximately the same length
		if (StringCompareUtils.lengthDifference(name1, name2) < MAX_LENGTH_DIFFERENCE) {
			sim = misspellingsSimilarity(name1, name2);
		}

		// similarity is returned only when it is acceptable
		// which means higher than a threshold
		if (sim > THRESHOLD_MISSPELLINGS) {
			return sim;
		} else {
			return 0;
		}
	}

	private double misspellingsSimilarity(String name1, String name2) {
		return comparator.getSimilarity(name1, name2);
	}

	@Override
	public double tokenAnalysis(String name1, String name2, EType eType) {
		etype = eType;

		List<FullName> nameList1 = nameManager.find(name1, eType);
		List<FullName> nameList2 = nameManager.find(name2, eType);
		// TODO
		// when a name is not present in the db, which means, namelist empty,
		// parse it for creating the name tokens

		double similarity = 0;
		List<List<Pair<String, String>>> listPairs = generateListPairs(
				nameList1, nameList2);

		for (List<Pair<String, String>> list : listPairs) {
			double totalSimilarity = 0;
			for (Pair<String, String> pair : list) {
				double simToken = stringSimilarity(pair.key, pair.value, eType);

				// TODO aggiungere anche variation sulle variant
				double simVariation = tokenVariant(pair.key, pair.value, etype);
				totalSimilarity += Math.max(simToken, simVariation)
						* ((double) pair.value.length() / name2.length());
			}
			if (totalSimilarity > similarity) {
				similarity = totalSimilarity;
			}
		}
		return similarity;
	}

	private double tokenVariant(String key, String value, EType e) {
		if (key.equals(value)) {
			return 1.0;
		}

		// cercare in traduzioni e varianti se c'e' un match
		boolean nameTranslation = individualNameDao.isTranslation(key, value);
		boolean triggerWordVariations = triggerWordDao.isVariation(key, value);
		double sim = 0.0;

		if (nameTranslation || triggerWordVariations) {
			sim = 1.0;
		} else {
			sim = triggerwordVariantSimilarity(key, value, e);
			if (sim == 0.0) {
				sim = tokenTranslationSimilarity(key, value, e);
			}
		}
		return sim;
	}

	private double tokenTranslationSimilarity(String key, String value, EType e) {
		double sim = 0.0;
		List<IndividualName> list = individualNameDao.findByNameEtype(key, e);

		if (list != null) {
			for (IndividualName n : list) {
				List<IndividualName> translations = individualNameDao
						.findTranslations(n);
				for (IndividualName t : translations) {
					double tmp = stringMatching(n.getName(), t.getName());
					if (tmp > sim) {
						sim = tmp;
					}
				}
			}
		}

		return sim;
	}

	private double triggerwordVariantSimilarity(String key, String value,
			EType e) {
		double sim = 0.0;
		List<TriggerWord> tList = triggerWordDao.findByTriggerWordEtype(key, e);
		if (tList != null) {
			for (TriggerWord t : tList) {
				List<TriggerWord> variations = triggerWordDao.findVariations(t);
				for (TriggerWord v : variations) {
					double tmp = stringMatching(v.getTriggerWord(), value);
					if (tmp > sim) {
						sim = tmp;
					}
				}
			}
		}
		return sim;
	}

	/**
	 * for all the possible combination of name1, name2 in the respective list,
	 * generates the token pairs to compare
	 *
	 * @param nameList1
	 *            list with FullName for name1
	 * @param nameList2
	 *            list with FullName for name2
	 * @return List of list of pairs of tokens
	 */
	private List<List<Pair<String, String>>> generateListPairs(
			List<FullName> nameList1, List<FullName> nameList2) {
		List<List<Pair<String, String>>> result = new ArrayList<>();

		for (FullName n1 : nameList1) {
			for (FullName n2 : nameList2) {
				result.add(generatePairs(n1, n2));
			}
		}
		return result;
	}

	/**
	 * generate pairs of token (name and trigger word) for the two input names,
	 * based on the name element or trigger word type they belong.
	 *
	 * @param n1
	 * @param n2
	 * @return
	 */
	private List<Pair<String, String>> generatePairs(FullName n1, FullName n2) {
		List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();

		for (NameElement nameElement : elementManager.findNameElement(etype)) {

			List<String> list1 = getTokenByElement(n1, nameElement);
			List<String> list2 = getTokenByElement(n2, nameElement);

			result.addAll(combinePairs(list1, list2));
		}

		for (TriggerWordType twtype : elementManager.findTriggerWordType(etype)) {
			if (twtype.isComparable()) {
				List<String> list1 = getTokenByElement(n1, twtype);
				List<String> list2 = getTokenByElement(n2, twtype);
				result.addAll(combinePairs(list1, list2));
			}
		}
		return result;
	}

	private List<Pair<String, String>> combinePairs(List<String> list1,
			List<String> list2) {

		List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
		if (list1.size() == list2.size() && list1.size() == 1) {
			result.add(new Pair(list1.get(0), list2.get(0)));
		} else {
			if (list1.size() != 0 && list2.size() != 0) {
				result.addAll(pairByLexicalGraphicOrder(list1, list2));
				// TODO if (list1.size() == 0) ???
			}
		}
		return result;
	}

	/**
	 * pair names from the two lists in pairs based on their lexical graphic
	 * order. In case the two lists have different length, the exceeded tokens
	 * are not considered.
	 *
	 * @param list1
	 * @param list2
	 * @return list of token pairs
	 */
	private List<Pair<String, String>> pairByLexicalGraphicOrder(
			List<String> list1, List<String> list2) {
		List<Pair<String, String>> result = new ArrayList<>();
		Collections.sort(list1);
		Collections.sort(list2);

		Iterator<String> i1 = list1.iterator();
		Iterator<String> i2 = list2.iterator();
		while (i1.hasNext() && i2.hasNext()) {
			result.add(new Pair<String, String>(i1.next(), i2.next()));
		}

		return result;
	}

	private List<String> getTokenByElement(FullName name,
			NameElement nameElement) {
		List<String> list = new ArrayList<String>();
		for (NameToken n : name.getNameTokens()) {
			if (n.getIndividualName().getNameElement().equals(nameElement)) {
				list.add(n.getIndividualName().getName());
			}
		}
		return list;
	}

	private List<String> getTokenByElement(FullName name, TriggerWordType type) {
		List<String> list = new ArrayList<String>();
		for (TriggerWordToken n : name.getTriggerWordTokens()) {
			if (n.getTriggerWord().getType().equals(type)) {
				list.add(n.getTriggerWord().getTriggerWord());
			}
		}
		return list;
	}

	@Override
	public double dictionaryLookup(String name1, String name2, EType eType) {
		etype = eType;
		double similarity = 0;

		if (name1 == null || name2 == null) {
			return 0.0;
		}

		// check exact tuple (name1,name2,eType)
		if (dictionaryExactLookup(name1, name2, eType)) {
			return 1.0;
		} else {
			// check variations on alternative names
			similarity = nameVariantSimilarity(name1, name2, eType);
		}
		return similarity > THRESHOLD_DICTIONARY ? similarity : 0;
	}

	/**
	 * check whether name2 is a variation of one variant of name1
	 *
	 * @param name1
	 * @param name2
	 * @param etype
	 * @return similarity of name2 with the variant of name1 (if any), 0
	 *         otherwise
	 */
	private double nameVariantSimilarity(String name1, String name2, EType etype) {
		double similarity = 0;

		// retrieves alternative names for n1
		List<FullName> variants = retrieveVariants(name1, etype);

		// order list of variantfor analyzing first the most probable match for
		// name2
		variants = orderVariant(variants);

		// for each variant of name1
		for (FullName name : variants) {
			// check exact string similarity
			if (stringEquality(name.getName(), name2)) {
				// TODO remove string equality check
				// never reached since exact variant is checked by
				// exactDictionaryLookup
				return 1.0;
			} else {
				similarity = stringSimilarity(name.getName(), name2, etype);
				// TODO define a correct threshold for misspellings and
				// dictionary
				if (similarity > THRESHOLD_MISSPELLINGS) {
					return similarity;
				}
			}
		}
		return 0;
	}

	/**
	 * order list of variant accordingly to some criteria for analyzing first
	 * variants which are highly probable to match name2
	 *
	 * @param variants
	 * @return list of names ordered
	 */
	private List<FullName> orderVariant(List<FullName> variants) {
		// TODO Auto-generated method stub
		return variants;
	}

	/**
	 * query the database for all the other names of entities which have "name"
	 * as name
	 *
	 *
	 * @param name
	 * @param etype
	 *            (optional)
	 * @return list of names
	 */
	private List<FullName> retrieveVariants(String name, EType etype) {
		return nameManager.retrieveVariants(name, etype);
	}

	/**
	 * check if the two strings correspond to the different names for the same
	 * entity.
	 *
	 * @param name1
	 * @param name2
	 * @param e
	 *            etype of the entity (optional)
	 * @return true if there is (at least) one entity which has name1 and name2
	 *         as possible names, false otherwise
	 */
	private boolean dictionaryExactLookup(String name1, String name2, EType e) {
		List<NamedEntity> list1, list2;

		// if etype specified, restrict the search to entities of etype e
		if (e != null) {
			list1 = entityManager.find(name1, e);
			list2 = entityManager.find(name2, e);
		}
		// otherwise do not consider the etype
		else {
			list1 = entityManager.find(name1);
			list2 = entityManager.find(name2);
		}

		// check if at least one entity is included in both lists
		for (NamedEntity en1 : list1) {
			for (NamedEntity en2 : list2) {
				if (en1.equals(en2)) {
					return true;
				}
			}
		}
		return false;
	}

	@Autowired
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Autowired
	public void setNameManager(NameManager nameManager) {
		this.nameManager = nameManager;
	}

	@Autowired
	public void setComparator(MisspellingsComparator comparator) {
		this.comparator = comparator;
	}

	@Autowired
	public void setElementManager(ElementManager elementManager) {
		this.elementManager = elementManager;
	}

	@Autowired
	public void setIndividualNameDao(IndividualNameDAO individualNameDao) {
		this.individualNameDao = individualNameDao;
	}

	@Autowired
	public void setTriggerWordDao(TriggerWordDAO triggerWordDao) {
		this.triggerWordDao = triggerWordDao;
	}
}
