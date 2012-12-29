package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.IndividualName;

import java.util.List;

public interface IndividualNameDAO {

	void save(IndividualName name);

	void update(IndividualName name);

	void delete(IndividualName name);

	IndividualName findById(int id);

	List<IndividualName> findByName(String name);

	List<IndividualName> findByNameEtype(String name, EType etype);

	boolean isTranslation(String name1, String name2);

	List<IndividualName> findTranslations(IndividualName name);

}