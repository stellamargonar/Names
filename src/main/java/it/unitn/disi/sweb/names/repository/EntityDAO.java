package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;

public interface EntityDAO {

	NamedEntity save(NamedEntity entity);

	NamedEntity update(NamedEntity entity);

	void delete(NamedEntity entity);

	NamedEntity findById(int id);

	List<NamedEntity> findByName(String name);

	List<NamedEntity> findByNameEtype(String name, EType etype);

	List<NamedEntity> findByUrl(String url);

	List<NamedEntity> findByNameUrl(String name,
			String url);

	List<NamedEntity> findByEtype(EType etype);

}