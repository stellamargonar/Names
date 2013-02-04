package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;
import java.util.Map;

public interface NameSearch {

	List<Map.Entry<String, Double>> nameSearch(String input);

	List<Map.Entry<NamedEntity, Double>> entityNameSearch(String input);
}
