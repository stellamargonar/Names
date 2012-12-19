package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWordType;

public interface TriggerWordTypeDAO {

	public void save(TriggerWordType twType);

	public void update(TriggerWordType twType);

	public void delete(TriggerWordType twType);

	public TriggerWordType findById(int id);

	public List<TriggerWordType> findAll();

	public TriggerWordType findByName(String name);

	public List<TriggerWordType> findByEType(EType etype);

	public void deleteAll();
}
