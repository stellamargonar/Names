package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.MisspellingsComparator;
import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.EntityDAO;
import it.unitn.disi.sweb.names.repository.FullNameDAO;
import it.unitn.disi.sweb.names.repository.NameElementDAO;
import it.unitn.disi.sweb.names.repository.TriggerWordTypeDAO;
import it.unitn.disi.sweb.names.service.NameMatch;
import it.unitn.disi.sweb.names.utils.Pair;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class NameMatchImpl implements NameMatch {

	private static int MAX_LENGTH_DIFFERENCE = 3;
	private static double THRESHOLD_MISSPELLINGS = 0.4;
	private static double THRESHOLD_DICTIONARY = THRESHOLD_MISSPELLINGS;

	private FullNameDAO nameDao;
	private NameElementDAO nameElementDao;
	private TriggerWordTypeDAO twtDao;
	private EntityDAO entityDao;

	private MisspellingsComparator comparator;

	private String name1;
	private String name2;
	private EType etype;

	public NameMatchImpl(String name1, String name2, EType eType) {
		this.name1 = name1;
		this.name2 = name2;
		this.etype = eType;
	}

	public NameMatchImpl() {
	}

	public double stringSimilarity(String name1, String name2, EType eType) {
		// TODO should we consider at this point the titles ecc, or compare only
		// the "name_to_compare" ?
		/*
		 * stringsim (obj 1, obj 2) { if (obj1 instance of string) nothing else
		 * if obj instance of some hash map with name -> field & position
		 */
		double similarity = 0;
		if (stringEquality(name1, name2))
			similarity = 1;
		else
			similarity = stringMatching(name1, name2, eType);
		return similarity;
	}

	public double stringSimilarity(FullName name1, FullName name2, EType eType) {
		return 0;
	}

	private boolean stringEquality(String string1, String string2) {
		if (string1 != null && string2 != null)
			return string1.equals(string2);
		else
			return false;
	}

	private double stringMatching(String name1, String name2, EType eType) {
		double sim = 0;
		if (StringCompareUtils.lengthDifference(name1, name2) < MAX_LENGTH_DIFFERENCE)
			sim = misspellingsSimilarity(name1, name2, eType);

		if (sim > THRESHOLD_MISSPELLINGS)
			return sim;
		else
			return 0;
	}

	private double misspellingsSimilarity(String string1, String string2,
			EType eType) {
		return comparator.getSimilarity(string1, string2);
	}

	public double tokenAnalysis(String name1, String name2, EType eType) {
		this.name1 = name1;
		this.name2 = name2;
		this.etype = eType;

		List<FullName> nameList1 = nameDao.findByNameEtype(name1, eType);
		List<FullName> nameList2 = nameDao.findByNameEtype(name2, eType);

		double similarity = 0;
		List<List<Pair<String, String>>> listPairs = generateListPairs(
				nameList1, nameList2);

		for (List<Pair<String, String>> list : listPairs) {
			double totalSimilarity = 0;
			for (Pair<String, String> pair : list) {
				double simToken = stringSimilarity(pair.key, pair.value, eType);
				totalSimilarity += simToken
						* ((double) (pair.value.length()) / name2.length());
			}
			if (totalSimilarity > similarity)
				similarity = totalSimilarity;
		}
		return similarity;
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

		for (FullName n1 : nameList1)
			for (FullName n2 : nameList2)
				result.add(generatePairs(n1, n2));
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

		for (NameElement nameElement : nameElementDao.findByEType(etype)) {

			List<String> list1 = getTokenByElement(n1, nameElement);
			List<String> list2 = getTokenByElement(n2, nameElement);

			result.addAll(combinePairs(list1, list2));
		}

		for (TriggerWordType twtype : twtDao.findByEType(etype)) {
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
		if (list1.size() == list2.size() && list1.size() == 1)
			result.add(new Pair(list1.get(0), list2.get(0)));
		else {
			if (list1.size() != 0 && list2.size() != 0)
				result.addAll(pairByLexicalGraphicOrder(list1, list2));
			// TODO if (list1.size() == 0) ???
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
		while (i1.hasNext() && i2.hasNext())
			result.add(new Pair<String, String>(i1.next(), i2.next()));

		return result;
	}

	private List<String> getTokenByElement(FullName name,
			NameElement nameElement) {
		List<String> list = new ArrayList<String>();
		for (NameToken n : name.getNameTokens())
			if (n.getIndividualName().getNameElement().equals(nameElement))
				list.add(n.getIndividualName().getName());
		return list;
	}

	private List<String> getTokenByElement(FullName name, TriggerWordType type) {
		List<String> list = new ArrayList<String>();
		for (TriggerWordToken n : name.getTriggerWordTokens())
			if (n.getTriggerWord().getType().equals(type))
				list.add(n.getTriggerWord().getTriggerWord());
		return list;
	}

	public static void main(String[] args) {
		NameMatch nameMatch = new NameMatchImpl();
		double sim = nameMatch.stringSimilarity("Stella", "Stello", null);
		System.out.println("(Stella, Stello): " + sim);

		sim = nameMatch.stringSimilarity("Fausto", "Fasuto", null);
		System.out.println("(Fausto, Fasuto): " + sim);

		sim = nameMatch.stringSimilarity("CANE", "ACEN", null);
		System.out.println("(CANE, ACEN): " + sim);

		sim = nameMatch.stringSimilarity("abc", "aef", null);
		System.out.println("(abc, aef): " + sim);
	}

	@Override
	public double dictionaryLookup(String name1, String name2, EType eType) {
		this.name1 = name1;
		this.name2 = name2;
		this.etype = eType;
		double similarity = 0;

		// check exact tuple (name1,name2,eType)
		if (dictionaryExactLookup(name1, name2, eType))
			return 1;
		else
			// check variations on alternative names
			similarity = nameVariantSimilarity(name1, name2, eType);
		return (similarity > THRESHOLD_DICTIONARY) ? similarity : 0;
	}

	private double nameVariantSimilarity(String n1, String n2, EType e) {
		double similarity = 0;
		// retrieves alternative names for n1
		List<FullName> variants = retrieveVariants(n1);

		// order list of variant accordingly to some criteria for analyzing
		// first
		// variants which are highly probable to match name2
		variants = orderVariant(variants);

		// for each variant of n1
		for (FullName name : variants) {
			// check plain string sim
			if (stringEquality(name.getName(), n2))
				return 1;
			else {
				similarity = stringSimilarity(name.getName(), n2, e);
				if (similarity > 0)
					return similarity;
			}
		}
		return 0;
	}

	private List<FullName> orderVariant(List<FullName> variants) {
		// TODO Auto-generated method stub
		return variants;
	}

	private List<FullName> retrieveVariants(String n1) {
		// select namedentity.names from NamedEntity as namedentity where GUID
		// in (select fullname.entity from FullName as fullname where name=
		// :name) and etype=:etype)
		return nameDao.findVariant(n1, etype);
	}

	private boolean dictionaryExactLookup(String n1, String n2, EType e) {
		List<NamedEntity> list1 = entityDao.findByNameEtype(n1, e);
		List<NamedEntity> list2 = entityDao.findByNameEtype(n2, e);

		for (NamedEntity en1 : list1)
			for (NamedEntity en2 : list2)
				if (en1.equals(en2))
					return true;
		return false;
	}

	@Autowired
	public void setComparator(MisspellingsComparator comparator) {
		this.comparator = comparator;
	}

	@Autowired
	public void setNameDAO(FullNameDAO dao) {
		nameDao = dao;
	}

	@Autowired
	public void setNameElementDao(NameElementDAO nameElementDao) {
		this.nameElementDao = nameElementDao;
	}

	@Autowired
	public void setTwtDao(TriggerWordTypeDAO twtDao) {
		this.twtDao = twtDao;
	}
}
