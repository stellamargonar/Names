package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;

import java.util.List;

public interface IndividualNameDAO {

	IndividualName save(IndividualName name);

	IndividualName update(IndividualName name);

	void delete(IndividualName name);

	IndividualName findById(int id);

	List<IndividualName> findByName(String name);

	List<IndividualName> findByNameEtype(String name, EType etype);

	boolean isTranslation(String name1, String name2);

	List<IndividualName> findTranslations(IndividualName name);

	List<IndividualName> findByNGram(int ngram, int diff);

	IndividualName findByNameElement(String name, NameElement element);

}