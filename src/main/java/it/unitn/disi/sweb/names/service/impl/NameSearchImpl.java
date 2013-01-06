package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.service.NameManager;
import it.unitn.disi.sweb.names.service.NameMatch;
import it.unitn.disi.sweb.names.service.NameSearch;
import it.unitn.disi.sweb.names.service.SearchType;
import it.unitn.disi.sweb.names.service.StatisticsManager;
import it.unitn.disi.sweb.names.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("nameSearch")
public class NameSearchImpl implements NameSearch {

	private static final double WEIGHT_EQUALS = 1.0;
	private static final double WEIGHT_REORDER = 0.8;
	private static final int MAXRESULT = 10;

	private NameManager nameManager;
	private NameMatch nameMatch;
	private StatisticsManager statManager;

	@Override
	public List<Pair<String, Double>> nameSearch(String input) {
		List<Pair<NamedEntity, Double>> result = entityNameSearch(input);
		return retrieveNames(result);
	}

	private List<Pair<String, Double>> retrieveNames(
			List<Pair<NamedEntity, Double>> list) {
		List<Pair<String, Double>> result = new ArrayList<Pair<String, Double>>();
		for (Pair<NamedEntity, Double> el : list) {
			// TODO rimettere lista nomi in namedentity

			List<FullName> names = nameManager.find(el.key);
			for (FullName n : names) {
				result.add(new Pair<String, Double>(n.getName(), el.value));
			}
		}

		return result;
	}

	@Override
	public List<Pair<NamedEntity, Double>> entityNameSearch(String input) {

		Map<NamedEntity, Double> listTopRank = searchTopRank(input);

		Map<NamedEntity, Double> listEquals = searchEquals(input);

		// tokens used also by other functions
		String[] tokens = generateTokens(input);

		Map<NamedEntity, Double> listReordering = searchReordered(tokens);

		Map<NamedEntity, Double> listMisspellings = searchMisspllings(input);

		Map<NamedEntity, Double> listToken = searchToken(tokens);

		return union(listTopRank, listEquals, listReordering, listMisspellings,
				listToken);
	}

	private Map<NamedEntity, Double> searchTopRank(String input) {
		Map<FullName, Double> names = statManager.retrieveTopResults(input,
				MAXRESULT);
		if (names == null) {
			return null;
		}

		Map<NamedEntity, Double> result = new HashMap<>(names.size());

		for (Entry<FullName, Double> entry : names.entrySet()) {
			result.put(entry.getKey().getEntity(), entry.getValue());
		}

		return result;
	}

	private String[] generateTokens(String input) {
		// TODO improve implementation
		return input.split(" ");
	}

	/**
	 * search for entities which have "input" as one of the possible names. The
	 * weight is equal 1.
	 *
	 * @param input
	 * @return list of entities
	 */
	private Map<NamedEntity, Double> searchEquals(String input) {
		List<FullName> found = nameManager.find(input, SearchType.TOCOMPARE);
		if (found == null || found.isEmpty()) {
			return null;
		}

		Map<NamedEntity, Double> result = new HashMap<>();
		for (FullName n : found) {
			result.put(n.getEntity(), WEIGHT_EQUALS);
		}

		return result;
	}

	private Map<NamedEntity, Double> searchReordered(String[] tokens) {
		String inputOrdered = "";
		List<String> tokensList = Arrays.asList(tokens);
		Collections.sort(tokensList);
		StringBuffer buf = new StringBuffer();
		for (String s : tokensList) {
			buf.append(s + " ");
		}
		inputOrdered = buf.toString();

		List<FullName> found = nameManager.find(inputOrdered,
				SearchType.NORMALIZED);

		Map<NamedEntity, Double> result = new HashMap<>();
		for (FullName n : found) {
			result.put(n.getEntity(), WEIGHT_REORDER);
		}

		// orderByField ( tokens )); TODO ???

		return result;
	}

	private Map<NamedEntity, Double> searchMisspllings(String input) {
		List<FullName> candidates = nameManager.find(input, SearchType.NGRAM);

		Map<NamedEntity, Double> result = new HashMap<>();
		for (FullName n : candidates) {
			double similarity = nameMatch.stringSimilarity(input, n.getName(),
					null);
			double oldSimilarity = result.get(n.getEntity()) != null ? result
					.get(n.getEntity()) : 0.0;
			if (similarity > 0.0 && oldSimilarity < similarity) {
				result.put(n.getEntity(), similarity);
			}

		}
		return result;
	}

	private Map<NamedEntity, Double> searchToken(String[] tokens) {
		HashMap<NamedEntity, Double> candidates = new HashMap<>();
		// TODO search also variations and translation of tokens (if this is the
		// case)
		int size = 0;
		for (String t : tokens) {
			List<NamedEntity> list = searchSingleToken(t);
			if (list != null && !list.isEmpty()) {
				size += list.size();
				for (NamedEntity r : list) {
					Double value = candidates.get(r);
					if (value == null) {
						value = 0.0;
					}
					candidates.put(r, value + 1);
				}
			}
		}

		Map<NamedEntity, Double> result = new HashMap<>();
		for (Entry<NamedEntity, Double> e : candidates.entrySet()) {
			result.put(e.getKey(), e.getValue() / size);
		}
		return result;
	}

	/**
	 * search for entity with names that contain the string in input
	 *
	 * @param t
	 * @return list of entities
	 */
	private List<NamedEntity> searchSingleToken(String input) {
		List<FullName> names = nameManager.find(input, SearchType.SINGLETOKEN);
		if (names == null || names.isEmpty()) {
			return null;
		}

		List<NamedEntity> result = new ArrayList<>();
		for (FullName n : names) {
			result.add(n.getEntity());
		}
		return result;
	}

	private List<Pair<NamedEntity, Double>> union(
			Map<NamedEntity, Double> listRank,
			Map<NamedEntity, Double> listEquals,
			Map<NamedEntity, Double> listReordering,
			Map<NamedEntity, Double> listMisspellings,
			Map<NamedEntity, Double> listToken) {
		Map<NamedEntity, Double> all = new HashMap<>();
		all = addToResult(listRank, all);
		all = addToResult(listEquals, all);
		all = addToResult(listReordering, all);
		all = addToResult(listMisspellings, all);
		all = addToResult(listToken, all);

		List<Pair<NamedEntity, Double>> result = new ArrayList<>(all.size());
		for (Entry<NamedEntity, Double> e : all.entrySet()) {
			result.add(new Pair<NamedEntity, Double>(e.getKey(), e.getValue()));
		}
		Collections.sort(result, new Comparator<Pair<NamedEntity, Double>>() {

			@Override
			public int compare(Pair<NamedEntity, Double> o1,
					Pair<NamedEntity, Double> o2) {
				return -1 * Double.compare(o1.value, o2.value);
			}
		});

		return result;
	}

	private Map<NamedEntity, Double> addToResult(Map<NamedEntity, Double> list,
			Map<NamedEntity, Double> all) {
		if (list == null || list.isEmpty()) {
			return all;
		}
		for (Entry<NamedEntity, Double> e : list.entrySet()) {
			if (all.containsKey(e.getKey())
					&& all.get(e.getKey()) < e.getValue()
					|| !all.containsKey(e.getKey())) {
				all.put(e.getKey(), e.getValue());
			}

		}
		return all;
	}

	@Autowired
	public void setNameManager(NameManager nameManager) {
		this.nameManager = nameManager;
	}

	@Autowired
	public void setNameMatch(NameMatch nameMatch) {
		this.nameMatch = nameMatch;
	}
	@Autowired
	public void setStatManager(StatisticsManager statManager) {
		this.statManager = statManager;
	}
}
