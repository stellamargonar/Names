package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;

import java.util.List;

public interface NameElementDAO {

	void save(NameElement field);

	void update(NameElement field);

	void delete(NameElement field);

	NameElement findById(int id);

	List<NameElement> findName(String name);

	List<NameElement> findAll();

	List<NameElement> findByEType(EType etype);

	NameElement findByNameEType(String name, EType etype);

	void deleteAll();
}