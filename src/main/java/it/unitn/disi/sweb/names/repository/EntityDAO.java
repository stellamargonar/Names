package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.NamedEntity;

public interface EntityDAO {

	public void save(NamedEntity entity);

	public void update(NamedEntity entity);

	public void delete(NamedEntity entity);

	public NamedEntity findById(int id);

	public List<NamedEntity> findByName(String name);

}
