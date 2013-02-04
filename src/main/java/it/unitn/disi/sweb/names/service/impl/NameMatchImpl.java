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
import it.unitn.disi.sweb.names.utils.RomanArabicNumberConverter;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Collections2;

@Service("nameMatcher")
public class NameMatchImpl implements NameMatch {

	private MisspellingsComparator comparator;

	private static int MAXLENGTHDIFFERENCE;
	private static double THRESHOLDMISSPELLINGS;
	private static double THRESHOLDDICTIONARY;
	private static double THRESHOLDTOKEN;
	private static double THRESHOLD;
	private static boolean FASTTESTING;

	private IndividualNameDAO individualNameDao;
	private TriggerWordDAO triggerWordDao;

	private EntityManager entityManager;
	private NameManager nameManager;
	private ElementManager elementManager;

	private boolean translatable1 = false;
	private boolean translatable2 = false;
	private boolean tryAllCombination = false;
	private boolean considerElement = false;

	private Log logger;

	private EType etype;
	public NameMatchImpl() {
	}

	@Override
	public double stringSimilarity(String name1, String name2, EType eType) {
		etype = eType;

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
		// try string equality with the entire name
		if (stringEquality(name1.getName(), name2.getName())) {
			return 1;
		}
		// try string equality with name to compare (no titles or other
		// triggerword)
		else if (stringEquality(name1.getNameToCompare(),
				name2.getNameToCompare())) {
			return 1;
		} else {
			// try string matching with entire names
			double sim1 = stringMatching(name1.getName(), name2.getName());
			if (sim1 > THRESHOLDMISSPELLINGS) {
				return sim1;
			} else {
				// try string matching with name to compare
				return stringMatching(name1.getNameToCompare(),
						name2.getNameToCompare());
			}
		}
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
		if (StringCompareUtils.lengthDifference(name1, name2) < MAXLENGTHDIFFERENCE) {
			sim = misspellingsSimilarity(name1, name2);
		}

		// similarity is returned only when it is acceptable
		// which means higher than a threshold
		if (sim > THRESHOLDMISSPELLINGS) {
			return sim;
		} else {
			return 0;
		}
	}

	/**
	 * returns the edit distance normalized on the lenght of the string for the
	 * two input string computed with a selected comparator. class for the
	 * comparator is setted in project.properties
	 *
	 * @param name1
	 *            first string
	 * @param name2
	 *            second string
	 * @return misspelling similarity computed through edit distance algorithms
	 */
	private double misspellingsSimilarity(String name1, String name2) {
		return comparator.getSimilarity(name1, name2);
	}

	@Override
	public double tokenAnalysis(FullName name1, FullName name2) {
		etype = name1.getEtype();
		Entry<List<FullName>, List<FullName>> lists = prepareTokenAnalysis(
				name1, name2);
		return tokenAnalysis(name1.getName(), name2.getName(), lists.getKey(),
				lists.getValue());
	}

	@Override
	public double tokenAnalysis(String name1, String name2, EType eType) {
		etype = eType;
		Entry<List<FullName>, List<FullName>> lists = prepareTokenAnalysis(
				name1, name2, eType);

		return tokenAnalysis(name1, name2, lists.getKey(), lists.getValue());

	}

	/**
	 * function used as preprocessing for token analysis. it creates two list of
	 * fullname containing the original fullname and all its variant stored
	 *
	 * @param name1
	 *            full name1
	 * @param name2
	 *            full name2
	 * @return a pair of lists of fullnames
	 */
	private Entry<List<FullName>, List<FullName>> prepareTokenAnalysis(
			FullName name1, FullName name2) {
		List<FullName> nameList1 = new ArrayList<>();
		nameList1.add(name1);

		List<FullName> nameList2 = new ArrayList<>();
		nameList2.add(name2);

		// adds their variant names
		nameList1 = addVariants(nameList1);
		nameList2 = addVariants(nameList2);

		return new AbstractMap.SimpleEntry<List<FullName>, List<FullName>>(
				nameList1, nameList2);
	}

	/**
	 * function used as preprocessing for token analysis. it creates two list of
	 * fullname containing the fullnames corresponding to the strings in input
	 * and all their variant stored.
	 *
	 * If no name can be found for the input strings, then it is created a new
	 * fullname (not stored in db)
	 *
	 * @param name1
	 *            string representing name1
	 * @param name2
	 *            string representing name2 full name2
	 * @return a pair of lists of fullnames
	 */
	private Entry<List<FullName>, List<FullName>> prepareTokenAnalysis(
			String name1, String name2, EType eType) {

		// search for already existing names in the db for the input stirngs
		List<FullName> nameList1 = nameManager.find(name1, eType);
		List<FullName> nameList2 = nameManager.find(name2, eType);

		// adds their variant names
		nameList1 = addVariants(nameList1);
		nameList2 = addVariants(nameList2);

		// one or both names are not in the database. need to tokenize the
		// string
		if (nameList1 == null || nameList1.isEmpty()) {
			// name1 not in the db

			FullName f = nameManager.buildFullName(name1, eType);
			nameList1 = new ArrayList<>();
			nameList1.add(f);
		}
		if (nameList2 == null || nameList2.isEmpty()) {
			FullName f = nameManager.buildFullName(name2, eType);
			nameList2 = new ArrayList<>();
			nameList2.add(f);
		}
		return new AbstractMap.SimpleEntry<List<FullName>, List<FullName>>(
				nameList1, nameList2);
	}

	/**
	 * list of the variant for the names in input
	 *
	 * @param nameList
	 * @return
	 */
	private List<FullName> addVariants(List<FullName> nameList) {
		Set<FullName> names = new HashSet<>();
		// TODO check if nameManager.retrieveVariant() is faster
		for (FullName n : nameList) {
			names.addAll(nameManager.find(n.getEntity()));
		}
		return new ArrayList<>(names);
	}

	/**
	 * function that executes the analysis of the tokens
	 *
	 * @param name1
	 *            string for name1
	 * @param name2
	 *            string for name2
	 * @param nameList1
	 *            list of fullnames and variants for name1
	 * @param nameList2
	 *            list of fullnames and variants for name2
	 * @return similarity computed through token comparison
	 */
	private double tokenAnalysis(String name1, String name2,
			List<FullName> nameList1, List<FullName> nameList2) {
		translatable1 = translatable2 = false;
		double similarity = 0.0;

		// before computing pair combination, tries to reorder the tokens and
		// consider only significant ones

		for (FullName n1 : nameList1) {
			for (FullName n2 : nameList2) {
				similarity = Math.max(
						stringSimilarity(n1.getNameToCompare(),
								n2.getNameNormalized(), etype),
						stringSimilarity(n1.getNameToCompare(),
								n2.getNameNormalized(), etype));
				if (similarity > THRESHOLD) {
					return similarity;
				}
			}
		}
		// generate pairs of tokens based on heuristics on the field name they
		// represent
		List<Map<String, String>> listPairs = generateListPairs(nameList1,
				nameList2);

		// finds the token combination which has the highest similarity, and
		// returns that value
		similarity = matchTokens(listPairs, name2);

		// if the previous combination failed, then tries to find the best token
		// combination for matching the input names
		if (!FASTTESTING) {
			tryAllCombination = true;
			// flag tryAllCombination allow to call pairAllCombination function
			// in this pair generation
			listPairs = generateListPairs(nameList1, nameList2);
			similarity = matchTokens(listPairs, name2);
			tryAllCombination = false;
		}
		return similarity;
	}

	/**
	 * finds the token combination which has the highest similarity, and returns
	 * that value. This is done computing the similarity for each token
	 * combination in input
	 *
	 * @param pairList
	 *            list of pair combination
	 * @param name2
	 *            name target
	 * @return similarity if high enough, 0.0 otherwise
	 */
	private double matchTokens(List<Map<String, String>> pairList, String name2) {
		double similarity = 0;
		for (Map<String, String> map : pairList) {
			double tmp = tokenSimilarity(map, name2);
			if (tmp > similarity) {
				similarity = tmp;
			}
		}
		return similarity > THRESHOLDTOKEN ? similarity : 0.0;
	}

	/**
	 * computes the similarity of a specific token combination. Similarity is
	 * computed with {@link #stringSimilarity(String, String, EType)
	 * stringSimilarity} and {@link #tokenVariant(String, String, EType)
	 * tokenVariant} for each pair of tokens, normalized on the lenght of the
	 * token and the target name
	 *
	 * @param map
	 *            token combination
	 * @param name2
	 *            name target
	 * @return similarity
	 */
	private double tokenSimilarity(Map<String, String> map, String name2) {
		double similarity = 0;

		// for each pair in the combination map
		for (Entry<String, String> pair : map.entrySet()) {
			// computes string similarity
			double simToken = stringSimilarity(pair.getKey(), pair.getValue(),
					etype);

			// TODO aggiungere anche variation sulle variant
			// computes token variant (alternative names and translations)
			double simVariation = tokenVariant(pair.getKey(), pair.getValue(),
					etype);

			// normalize on the target name length
			similarity += Math.max(simToken, simVariation)
					* ((double) pair.getValue().length() / name2.length());
		}
		return similarity;
	}

	/**
	 * computes the similarity of the two token considering the alternative
	 * names and translation (with respect to misspellings)
	 *
	 * @param key
	 *            source token
	 * @param value
	 *            target token
	 * @param e
	 *            etype
	 * @return token similarity
	 */
	private double tokenVariant(String key, String value, EType e) {
		// stirng equality for avoiding meaningless computations
		if (key.equals(value)) {
			return 1.0;
		}

		// check roman numbers
		double sim = checkRomanNumbers(key, value);
		if (sim > 0) {
			return sim;
		}

		// determine if it is translatable and if the two tokens are translation
		// of each other
		boolean nameTranslation = translatable1 || translatable2
				? individualNameDao.isTranslation(key.toLowerCase(),
						value.toLowerCase())
				: false;
		// check for trigger word variations
		boolean triggerWordVariations = triggerWordDao.isVariation(
				key.toLowerCase(), value.toLowerCase());

		if (nameTranslation || triggerWordVariations) {
			sim = 1.0;
		} else {
			// search for variant and alternatives with respect to misspellings
			sim = triggerwordVariantSimilarity(key, value, e);
			if (sim == 0.0) {
				sim = tokenTranslationSimilarity(key, value, e);
			}
		}
		return sim;
	}

	public double checkRomanNumbers(String key, String value) {
		int keyNr = 0, valueNr = 0;
		try {
			keyNr = Integer.parseInt(key);
		} catch (NumberFormatException ex) {
		}
		try {
			valueNr = Integer.parseInt(value);
		} catch (NumberFormatException ex) {
		}

		if (keyNr == 0) {
			keyNr = RomanArabicNumberConverter.RomanToArabic(key);
			if (keyNr == 0) {
				return 0;
			}
		}
		if (valueNr == 0) {
			valueNr = RomanArabicNumberConverter.RomanToArabic(value);
			if (valueNr == 0) {
				return 0;
			}
		}
		if (keyNr == valueNr) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	/**
	 * search if a translation of {@code key} matches with {@code value}
	 *
	 * @param key
	 *            source token
	 * @param value
	 *            target token
	 * @param e
	 *            etype
	 * @return
	 */
	private double tokenTranslationSimilarity(String key, String value, EType e) {
		double sim = 0.0;
		List<IndividualName> list = individualNameDao.findByNameEtype(
				key.toLowerCase(), e);

		if (list != null) {
			for (IndividualName n : list) {
				List<IndividualName> translations = individualNameDao
						.findTranslations(n);
				for (IndividualName t : translations) {
					double tmp = stringMatching(t.getName(), value);
					if (tmp > sim) {
						sim = tmp;
					}
				}
			}
		}
		return sim;
	}

	/**
	 * search if a variation of trigger word {@code key} matches with
	 * {@code value}
	 *
	 * @param key
	 *            source token
	 * @param value
	 *            target token
	 * @param e
	 *            etype
	 * @return
	 */
	private double triggerwordVariantSimilarity(String key, String value,
			EType e) {
		double sim = 0.0;
		List<TriggerWord> tList = triggerWordDao.findByTriggerWordEtype(
				key.toLowerCase(), e);

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
	private List<Map<String, String>> generateListPairs(
			List<FullName> nameList1, List<FullName> nameList2) {
		List<Map<String, String>> result = new ArrayList<>();

		// for all the possible pair of fullnames, generate the corresponding
		// combination of pairs
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
	private Map<String, String> generatePairs(FullName n1, FullName n2) {
		Map<String, String> result = new HashMap<>();

		// TODO change position of this computation.
		// should be done when the names are matched (in matchTokens)
		translatable1 = nameManager.translatable(n1);
		translatable2 = nameManager.translatable(n2);

		if (tryAllCombination) {
			result.putAll(pairByBestCombination(n1, n2));
		} else if (considerElement) {

			// for each element of the given etype
			for (NameElement nameElement : elementManager
					.findNameElement(etype)) {

				// combine tokens of the same name element
				List<String> list1 = getTokenByElement(n1, nameElement);
				List<String> list2 = getTokenByElement(n2, nameElement);
				result.putAll(combinePairs(list1, list2));
			}

			// same for trigger words
			for (TriggerWordType twtype : elementManager
					.findTriggerWordType(etype)) {
				if (twtype.isComparable()) {
					List<String> list1 = n1.getTriggerWordTokens() == null
							? null
							: getTokenByElement(n1, twtype);
					List<String> list2 = n2.getTriggerWordTokens() == null
							? null
							: getTokenByElement(n2, twtype);
					result.putAll(combinePairs(list1, list2));
				}
			}
		} else {
			List<String> list1 = getAllTokens(n1);
			List<String> list2 = getAllTokens(n2);
			result.putAll(combinePairs(list1, list2));
		}
		return result;
	}

	private List<String> getAllTokens(FullName name) {
		List<String> list = new ArrayList<>();
		for (NameToken nt : name.getNameTokens()) {
			String tok = nt.getIndividualName().getName();
			if (!tok.isEmpty()) {
				list.add(tok);
			}
		}
		if (name.getTriggerWordTokens() != null) {
			for (TriggerWordToken t : name.getTriggerWordTokens()) {
				String tok = t.getTriggerWord().getTriggerWord();
				if (!tok.isEmpty()) {
					list.add(tok);
				}
			}
		}
		return list;
	}

	/**
	 * given two list of strings, this function combines their elements
	 * according to some criteria, and return this combination as a Map of
	 * strings
	 *
	 * @param list1
	 * @param list2
	 * @return
	 */
	private Map<String, String> combinePairs(List<String> list1,
			List<String> list2) {
		Map<String, String> result = new HashMap<>();
		if (list1 == null || list2 == null) {
			return result;
		}

		if (list1.size() == list2.size() && list1.size() == 1) {
			result.put(list1.get(0), list2.get(0));
		} else {
			if (list1.size() != 0 && list2.size() != 0) {
				result.putAll(pairByLexicalGraphicOrder(list1, list2));
			} else {
				if (list1.size() == 0) {
					for (String s : list2) {
						result.put("", s);
					}
				} else {
					for (String s : list1) {
						result.put(s, "");
					}
				}
			}
		}
		return result;
	}

	/**
	 * tries all the possible combination of tokens and returns the one with the
	 * highest similarity.
	 *
	 * @param n1
	 *            fullname source
	 * @param n2
	 *            fullname target
	 * @return combinatio of tokens
	 */
	private Map<String, String> pairByBestCombination(FullName n1, FullName n2) {
		List<String> list1 = Arrays.asList(StringCompareUtils.generateTokens(n1
				.getName()));
		List<String> list2 = Arrays.asList(StringCompareUtils.generateTokens(n2
				.getName()));
		List<List<String>> permutations = getAllCombinations(list1, list2);

		double similarity = 0.0;
		Map<String, String> bestMatch = null;
		for (List<String> p : permutations) {
			Map<String, String> map = new HashMap<>();
			for (int i = 0; i < list1.size(); i++) {
				map.put(list1.get(i), p.get(i));
			}
			double tmp = tokenSimilarity(map, n2.getName());
			if (tmp > similarity) {
				similarity = tmp;
				bestMatch = map;
			}
		}
		return bestMatch;
	}

	/**
	 * computes all the possible combination of tokens. If the number of tokens
	 * is different in the two names the function uses the first name as base
	 * and if necessary adds emtpy tokens to the second list
	 *
	 * @param list1
	 *            list of tokens for source name
	 * @param list2
	 *            list of tokens for target name
	 * @return all possible permutations of tokens
	 */
	private List<List<String>> getAllCombinations(List<String> list1,
			List<String> list2) {
		List<List<String>> allCombinations = new ArrayList<>();

		int k = list1.size();
		// find all subset of size k of list2
		List<String> tmpList = new ArrayList<>(list2);
		while (tmpList.size() < k) {
			tmpList.add("");
		}

		Set<Collection<String>> subsets = getSubsets(tmpList, k);
		// add all permutation
		for (Collection<String> subset : subsets) {
			allCombinations.addAll(Collections2.orderedPermutations(subset));
		}
		return allCombinations;
	}

	/**
	 * returns all the possible subsets of size {@code k} for the input list.
	 *
	 * @param set
	 * @param k
	 *            size of subsets
	 * @return
	 */
	private <T> Set<Collection<T>> getSubsets(List<T> set, int k) {
		Set<Collection<T>> subset = new HashSet<>();
		List<T> element = new ArrayList<>();
		List<String> binary = new ArrayList<>();
		double TotalSubsets = Math.pow(2, set.size());

		for (int i = 1; i < TotalSubsets; i++) {
			binary = new ArrayList<String>(Arrays.asList(Integer
					.toBinaryString(i).split("")));

			binary.remove(0);
			while (binary.size() < set.size()) {
				binary.add(0, "0");
			}
			if (Collections.frequency(binary, "1") == k) {
				for (int j = 0; j < binary.size(); j++) {
					if (binary.get(j).equals("1")) {
						element.add(set.get(j));
					}
				}
				subset.add(element);
				element = new ArrayList<>();
			}
		}
		return subset;
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
	private Map<String, String> pairByLexicalGraphicOrder(List<String> list1,
			List<String> list2) {
		Map<String, String> result = new HashMap<>();

		// creates maps based on the first letter for the strings in the two
		// lists
		Map<Character, List<String>> alpha1 = new HashMap<>();
		Map<Character, List<String>> alpha2 = new HashMap<>();
		for (String s : list1) {
			char c = Character.toLowerCase(s.charAt(0));
			List<String> l = alpha1.get(c);
			if (l == null || l.isEmpty()) {
				l = new ArrayList<>();
			}
			l.add(s);
			alpha1.put(c, l);
		}
		for (String s : list2) {
			char c = Character.toLowerCase(s.charAt(0));
			List<String> l = alpha2.get(c);
			if (l == null || l.isEmpty()) {
				l = new ArrayList<>();
			}
			l.add(s);
			alpha2.put(c, l);
		}

		for (Character c : alpha1.keySet()) {
			List<String> l1 = alpha1.get(c);
			List<String> l2 = alpha2.get(c);
			if (l2 != null && !l2.isEmpty()) {
				result.putAll(pairSameLetter(l1, l2));
			}
		}

		return result;
	}

	/**
	 * @param result
	 * @param l1
	 * @param l2
	 */
	private Map<String, String> pairSameLetter(List<String> l1, List<String> l2) {
		Map<String, String> result = new HashMap<>();
		Collections.sort(l1, new PairComparator());
		Collections.sort(l2, new PairComparator());

		Iterator<String> it1 = l1.iterator();
		Iterator<String> it2 = l2.iterator();

		// OLD VERSION, ONLY WITH LEXICALGRAPHIC ORDER
		while (it1.hasNext() && it2.hasNext()) {
			result.put(it1.next(), it2.next());
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
	public double dictionaryLookup(FullName name1, FullName name2) {
		etype = name1.getEtype();

		// TODO check if it makes sense to just compare the entities in the
		// fullname object, or if this solution is preferred when new names are
		// added to the system

		if (name1.getEntity() != null && name2.getEntity() != null
				&& name1.getEntity().equals(name2.getEntity())) {
			return 1.0;
		}
		return dictionaryLookup(name1.getName(), name2.getName(), etype);
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
		return similarity > THRESHOLDDICTIONARY ? similarity : 0;
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
				if (similarity > THRESHOLDMISSPELLINGS * THRESHOLDDICTIONARY) {
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

	@Resource(name = "${misspellings.comparator}")
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

	@Value("${threshold.lenght}")
	public void setMaxLengthDifference(int m) {
		MAXLENGTHDIFFERENCE = m;
	}

	@Value("${threshold.misspellings}")
	public void setThresholdMisspellings(double t) {
		THRESHOLDMISSPELLINGS = t;
	}
	@Value("${threshold.dictionary}")
	public void setThresholdDictionary(double t) {
		THRESHOLDDICTIONARY = t;
	}
	@Value("${threshold.token}")
	public void setThresholdToken(double t) {
		THRESHOLDTOKEN = t;
	}

	@Value("${threshold}")
	public void setThreshold(double t) {
		THRESHOLD = t;
	}

	@Value("${test.fasttest}")
	public void setFastTesting(boolean f) {
		FASTTESTING = f;
	}

	@Autowired
	public void setLogger(Log logger) {
		this.logger = logger;
	}

	@Override
	public double match(String name1, String name2, EType etype) {
		long start = System.currentTimeMillis();
		long time = 0;
		double sim1, sim2, sim3;
		double similarity = 0.0;

		sim1 = stringSimilarity(name1, name2, etype);
		time = System.currentTimeMillis() - start;
		// logger.debug("StringSimilarity " + time);

		if (sim1 >= THRESHOLDMISSPELLINGS) {
			return sim1;
		} else {
			start = System.currentTimeMillis();
			sim2 = dictionaryLookup(name1, name2, etype);
			time = System.currentTimeMillis() - start;
			// logger.debug("DictionaryLookup: " + time);

			if (sim2 >= THRESHOLDDICTIONARY) {
				return sim2;
			} else {
				start = System.currentTimeMillis();
				sim3 = tokenAnalysis(name1, name2, etype);
				time = System.currentTimeMillis() - start;
				// logger.debug("TokenAnalysis " + time);
			}
		}
		similarity = Math.max(Math.max(sim1, sim2), sim3);
		return similarity >= THRESHOLD ? similarity : 0.0;
	}

	@Override
	public double match(FullName name1, FullName name2) {
		long start = System.currentTimeMillis();
		long time = 0;
		double sim1, sim2, sim3;
		double similarity = 0.0;

		sim1 = stringSimilarity(name1, name2, etype);
		time = System.currentTimeMillis() - start;
		logger.debug("StringSimilarity " + time);

		if (sim1 >= THRESHOLDMISSPELLINGS) {
			return sim1;
		} else {
			start = System.currentTimeMillis();
			sim2 = dictionaryLookup(name1, name2);
			time = System.currentTimeMillis() - start;
			logger.debug("DictionaryLookup: " + time);

			if (sim2 >= THRESHOLDDICTIONARY) {
				return sim2;
			} else {
				start = System.currentTimeMillis();
				sim3 = tokenAnalysis(name1, name2);
				time = System.currentTimeMillis() - start;
				logger.debug("TokenAnalisys " + time);
			}
		}
		similarity = Math.max(Math.max(sim1, sim2), sim3);
		return similarity >= THRESHOLD ? similarity : 0.0;
	}

	private class PairComparator implements Comparator<String> {

		@Override
		public int compare(String s1, String s2) {
			int n1 = StringCompareUtils.computeNGram(s1);
			int n2 = StringCompareUtils.computeNGram(s2);
			return Integer.compare(n1, n2);
		}

	}

}
