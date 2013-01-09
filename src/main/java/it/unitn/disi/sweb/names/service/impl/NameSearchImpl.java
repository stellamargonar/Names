package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.service.ElementManager;
import it.unitn.disi.sweb.names.service.NameManager;
import it.unitn.disi.sweb.names.service.NameMatch;
import it.unitn.disi.sweb.names.service.NameSearch;
import it.unitn.disi.sweb.names.service.SearchType;
import it.unitn.disi.sweb.names.service.StatisticsManager;
import it.unitn.disi.sweb.names.utils.Pair;
import it.unitn.disi.sweb.names.utils.StringCompareUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("nameSearch")
public class NameSearchImpl implements NameSearch {

	private static final double WEIGHT_EQUALS = 1.0;
	private static final double WEIGHT_REORDER = 0.8;
	private static final int MAXRESULT = 5;

	private NameManager nameManager;
	private NameMatch nameMatch;
	private StatisticsManager statManager;
	private ElementManager elementManager;

	@Override
	public List<Pair<String, Double>> nameSearch(String input) {
		List<Pair<NamedEntity, Double>> result = entityNameSearch(input);
		return retrieveNames(result);
	}

	/**
	 * given a list of entities labeld with a weight, returns the list of
	 * fullnames corresponding to the input entities, labeled with the
	 * respective weight
	 *
	 * @param list
	 *            of pair of entity, weight
	 * @return list of pair of fullname, weight
	 */
	private List<Pair<String, Double>> retrieveNames(
			List<Pair<NamedEntity, Double>> list) {

		List<Pair<String, Double>> result = new ArrayList<Pair<String, Double>>();

		// for each entity in the list
		for (Pair<NamedEntity, Double> el : list) {
			// TODO rimettere lista nomi in namedentity class

			// retrieves from the db its names, and add them to the result list,
			// with the entity's weight
			List<FullName> names = nameManager.find(el.key);
			for (FullName n : names) {
				result.add(new Pair<String, Double>(n.getName(), el.value));
			}
		}
		return result;
	}

	@Override
	public List<Pair<NamedEntity, Double>> entityNameSearch(String input) {

		// search for top results based on previous search statistics
		Map<NamedEntity, Double> listTopRank = searchTopRank(input);

		// search for entities with that exact name
		Map<NamedEntity, Double> listEquals = searchEquals(input);

		// tokens used also by other functions
		String[] tokens = StringCompareUtils.generateTokens(input);

		// search for entities with name equals to the input with tokens in
		// different order
		Map<NamedEntity, Double> listReordering = searchReordered(tokens);

		// search for names that match input wrt misspellings
		Map<NamedEntity, Double> listMisspellings = searchMisspellings(input);

		// search for names which match tokens of the input name
		Map<NamedEntity, Double> listToken = searchToken(tokens);

		return union(listTopRank, listEquals, listReordering, listMisspellings,
				listToken);
	}

	/**
	 * search for the most selected result name (based on previous user
	 * statistics) for the query "input"
	 *
	 * @param input
	 * @return list of top ranked results
	 */
	private Map<NamedEntity, Double> searchTopRank(String input) {
		if (input == null || input.isEmpty()) {
			return null;
		}

		// query the database for the most selected result for query "input"
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



	/**
	 * search for entities which have "input" as one of the possible names. The
	 * weight is equal 1.
	 *
	 * @param input
	 * @return list of entities
	 */
	private Map<NamedEntity, Double> searchEquals(String input) {
		if (input == null || input.isEmpty()) {
			return null;
		}

		// search for names where name_to_compare equals to input
		List<FullName> foundCompare = nameManager.find(input,
				SearchType.TOCOMPARE);
		// search also in the entire string representing the name
		List<FullName> foundEquals = nameManager.find(input);
		List<FullName> found = foundCompare != null
				? foundCompare
				: new ArrayList<FullName>();
		if (foundEquals != null) {
			found.addAll(foundEquals);
		}

		if (found.isEmpty()) {
			return null;
		}

		// add names to the result list with max weight
		Map<NamedEntity, Double> result = new HashMap<>();
		for (FullName n : found) {
			result.put(n.getEntity(), WEIGHT_EQUALS);
		}

		return result;
	}

	/**
	 * search for the names that are equals to the input with tokens reordered
	 * by lexical graphic order
	 *
	 * @param tokens
	 *            array of string representing the name tokens
	 * @return entities that match the reordered name
	 */
	private Map<NamedEntity, Double> searchReordered(String[] tokens) {
		if (tokens == null || tokens.length == 0) {
			return null;
		}

		// orders tokens
		List<String> tokensList = Arrays.asList(tokens);
		Collections.sort(tokensList);

		// reconverts tokens to a single string
		StringBuffer buf = new StringBuffer();
		for (String s : tokensList) {
			buf.append(s + " ");
		}
		String inputOrdered = buf.toString();
		if (inputOrdered.startsWith(" ")) {
			inputOrdered = inputOrdered.substring(1);
		}
		if (inputOrdered.endsWith(" ")) {
			inputOrdered = inputOrdered.substring(0, inputOrdered.length() - 1);
		}

		// search for entities using the new generated string
		List<FullName> found = nameManager.find(inputOrdered,
				SearchType.NORMALIZED);
		if (found == null || found.size() == 0) {
			return null;
		}

		Map<NamedEntity, Double> result = new HashMap<>();
		for (FullName n : found) {
			result.put(n.getEntity(), WEIGHT_REORDER);
		}

		// TODO implement order search based on fields
		// orderByField ( tokens ));

		return result;
	}

	/**
	 * search for names that match the input query with respect to misspellings.
	 * The search function uses a ngram based index to retrieve the most
	 * probable name that match, and then uses the misspelling similarity
	 * function fron NameMatch to identify the real match
	 *
	 * @param input
	 * @return entities which names match the input query
	 */
	private Map<NamedEntity, Double> searchMisspellings(String input) {
		if (input == null || input.isEmpty()) {
			return null;
		}

		// search for names which ngram is close to the one of the input query
		List<FullName> candidates = nameManager.find(input, SearchType.NGRAM);
		if (candidates == null || candidates.size() == 0) {
			return null;
		}

		// add to the result list only the ones which really match the input
		// query
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
		if (result.size() == 0) {
			return null;
		}
		return result;
	}

	/**
	 * search for names that match single tokens from the input query, using
	 * also misspellings and token variations
	 *
	 * @param tokens
	 * @return
	 */
	private Map<NamedEntity, Double> searchToken(String[] tokens) {
		if (tokens == null || tokens.length == 0) {
			return null;
		}

		HashMap<NamedEntity, Double> candidates = new HashMap<>();
		// TODO search also variations and translation of tokens (if this is the
		// case)
		int size = 0;
		for (String t : tokens) {
			if (!t.equals("")) {
				List<NamedEntity> list = searchSingleToken(t);
				list.addAll(searchTokenMisspellings(t));
				list.addAll(searchTokenVariations(t));

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
		}
		if (candidates.isEmpty()) {
			return null;
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
		List<NamedEntity> result = new ArrayList<>();

		if (names == null || names.isEmpty()) {
			return result;
		}

		for (FullName n : names) {
			result.add(n.getEntity());
		}
		return result;
	}

	/**
	 * search for names that contain the input token wrt misspellings
	 *
	 * @param token
	 * @return
	 */
	private Collection<NamedEntity> searchTokenMisspellings(String token) {
		Set<NamedEntity> result = new HashSet<>();
		List<Object> resultToken = new ArrayList<>();
		List<Object> candidates = elementManager.findMisspellings(token);

		for (Object o : candidates) {
			if (o instanceof IndividualName) {
				IndividualName i = (IndividualName) o;
				if (nameMatch.stringSimilarity(token, i.getName(), null) > 0) {
					resultToken.add(i);
				}
			} else if (o instanceof TriggerWord) {
				TriggerWord t = (TriggerWord) o;
				if (nameMatch.stringSimilarity(token, t.getTriggerWord(), null) > 0) {
					resultToken.add(t);
				}
			}
		}

		for (Object o : resultToken) {
			if (o instanceof IndividualName) {
				IndividualName i = (IndividualName) o;
				for (FullName f : elementManager.find(i)) {
					result.add(f.getEntity());
				}
			} else if (o instanceof TriggerWord) {
				TriggerWord t = (TriggerWord) o;
				for (FullName f : elementManager.find(t)) {
					result.add(f.getEntity());
				}
			}
		}

		return result;
	}

	private Collection<NamedEntity> searchTokenVariations(String t) {
		List<TriggerWord> triggerWords = elementManager.findTriggerWord(t);
		Set<NamedEntity> result = new HashSet<>();
		Set<FullName> names = new HashSet<>();

		if (triggerWords == null || triggerWords.size() == 0) {
			return result;
		}
		for (TriggerWord w : triggerWords) {
			for (TriggerWord var : w.getVariations()) {
				names.addAll(elementManager.find(var));
			}
		}

		for (FullName f : names) {
			result.add(f.getEntity());
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
	@Autowired
	public void setElementManager(ElementManager elementManager) {
		this.elementManager = elementManager;
	}
}
