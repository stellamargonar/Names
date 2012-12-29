package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;

public interface FullNameDAO {

	FullName save(FullName fullname);

	FullName update(FullName fullname);

	void delete(FullName fullname);

	FullName findById(int id);

	List<FullName> findByName(String name);

	List<FullName> findByNameNormalized(String name);

	List<FullName> findByNameToCompare(String name);

	List<FullName> findByNameEtype(String name, EType etype);

	List<FullName> findByEntity(NamedEntity entity);

	FullName findByEntityName(String name, NamedEntity entity);

	List<FullName> findVariant(String name, EType etype);

	List<FullName> findByToken(String token);

	List<FullName> findByNgram(int ngram, int diff);

}