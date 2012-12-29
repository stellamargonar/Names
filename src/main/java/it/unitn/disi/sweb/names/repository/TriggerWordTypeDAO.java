package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWordType;

import java.util.List;

public interface TriggerWordTypeDAO {

	void save(TriggerWordType twType);

	void update(TriggerWordType twType);

	void delete(TriggerWordType twType);

	TriggerWordType findById(int id);

	List<TriggerWordType> findAll();

	TriggerWordType findByName(String name);

	List<TriggerWordType> findByEType(EType etype);

	void deleteAll();
}
