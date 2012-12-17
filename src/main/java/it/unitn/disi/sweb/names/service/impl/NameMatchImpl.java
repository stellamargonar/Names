package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.MisspellingsComparator;
import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.repository.FullNameDAO;
import it.unitn.disi.sweb.names.service.NameMatch;
import it.unitn.disi.sweb.names.utils.JaroWinkler;
import it.unitn.disi.sweb.names.utils.Pair;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.ArrayList;
import java.util.List;

public class NameMatchImpl implements NameMatch {

	private static int MAX_LENGTH_DIFFERENCE = 3;
	private static double THRESHOLD_MISSPELLINGS = 0.4;

	private FullNameDAO nameDao;

	public void setNameDAO(FullNameDAO dao) {
		nameDao = dao;
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
		MisspellingsComparator comparator = JaroWinkler.getInstance();
		return comparator.getSimilarity(string1, string2);
	}

	public double tokenAnalysis(String name1, String name2, EType eType) {
		List<FullName> nameList1 = nameDao.findByNameEtype(name1, eType);
		List<FullName> nameList2 = nameDao.findByNameEtype(name2, eType);

		double similarity = 0;

		for (FullName fullName1 : nameList1)
			for (FullName fullName2 : nameList2) {
				List<NameToken> nameTokens1 = fullName1.getNameTokensOrdered();
				List<NameToken> nameTokens2 = fullName2.getNameTokensOrdered();

				List<TriggerWordToken> twTokens1 = fullName1
						.getTriggerWordTokensOrdered();
				List<TriggerWordToken> twTokens2 = fullName2
						.getTriggerWordTokensOrdered();
				double totalSimilarity = 0;

				List<Pair<String, String>> pairs = generatePairs(nameTokens1,
						twTokens1, nameTokens2, twTokens2);
				for (Pair<String, String> pair : pairs) {
					double simToken = stringSimilarity(pair.key, pair.value,
							eType);
					totalSimilarity += simToken
							* ((double) (pair.value.length()) / name2.length());
				}
				if (totalSimilarity > similarity)
					similarity = totalSimilarity;
			}
		return similarity;
	}

	private List<Pair<String, String>> generatePairs(
			List<NameToken> nameTokens1, List<TriggerWordToken> twTokens1,
			List<NameToken> nameTokens2, List<TriggerWordToken> twTokens2) {

		List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
		// TODO uncomment
		// for (NameField fieldType : nameDao.getNameFieldList()) {
		// List<NameToken> list1 = new ArrayList<NameToken>();
		// for (NameToken n : nameTokens1)
		// if (n.getNameField().equals(fieldType))
		// list1.add(n);
		// List<NameToken> list2 = new ArrayList<NameToken>();
		// for (NameToken n : nameTokens2)
		// if (n.getNameField().equals(fieldType))
		// list2.add(n);
		// if (list1.size() == list1.size() && list1.size() == 1)
		// result.add(new Pair(list1.get(0).getIndividualName().getName(),
		// list2.get(0).getIndividualName().getName()));
		// else {
		// // ordinare per ordine alfabetico
		// }
		// }

		// same for trigger words, or similar

		return result;
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
		// TODO Auto-generated method stub
		return 0;
	}
}
