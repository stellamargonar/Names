package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.utils.Pair;

import java.util.List;

public interface NameSearch {

	List<Pair<String, Double>> nameSearch(String input);

	List<Pair<NamedEntity, Double>> entityNameSearch(String input);
}
