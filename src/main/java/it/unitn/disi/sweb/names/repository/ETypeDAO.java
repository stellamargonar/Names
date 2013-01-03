package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;

import java.util.List;

public interface ETypeDAO {

	EType save(EType e);

	EType update(EType e);

	void delete(EType e);

	EType findById(int id);

	EType findByName(String name);

	List<EType> findAll();

	void deleteAll();
}