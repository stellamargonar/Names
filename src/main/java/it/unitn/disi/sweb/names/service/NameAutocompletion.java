package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.utils.Pair;

import java.util.List;

public interface NameAutocompletion {

	/**
	 * search for names which start with the input prefix, ordered by similarity
	 * / ranking
	 *
	 * @param prefix
	 * @return
	 */
	List<Pair<FullName, Double>> searchNamePrefix(String prefix);

	/**
	 *
	 * search for names of the specified etype which start with the input
	 * prefix, , ordered by similarity / ranking
	 *
	 * @param prefix
	 * @param etype
	 * @return
	 */
	List<Pair<FullName, Double>> searchNamePrefix(String prefix, EType etype);

}
