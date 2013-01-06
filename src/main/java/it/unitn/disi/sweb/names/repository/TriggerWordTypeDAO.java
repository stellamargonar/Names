package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWordType;

import java.util.List;

public interface TriggerWordTypeDAO {

	TriggerWordType save(TriggerWordType twType);

	TriggerWordType update(TriggerWordType twType);

	void delete(TriggerWordType twType);

	TriggerWordType findById(int id);

	List<TriggerWordType> findAll();

	List<TriggerWordType> findByName(String name);

	List<TriggerWordType> findByEType(EType etype);
	TriggerWordType findByNameEType(String name, EType etype);

	void deleteAll();
}
