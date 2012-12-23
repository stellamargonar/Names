package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.model.EType;

public interface EntityDAO {

	public NamedEntity save(NamedEntity entity);

	public void update(NamedEntity entity);

	public void delete(NamedEntity entity);

	public NamedEntity findById(int id);

	public List<NamedEntity> findByName(String name);

	public List<NamedEntity> findByNameEtype(String name, EType etype);

	public List<NamedEntity> findByUrl(String url);

	public List<NamedEntity> findByNameUrl(String name,
			String url);

	public List<NamedEntity> findByEtype(EType etype);

}
