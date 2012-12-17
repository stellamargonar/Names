package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;

import java.util.List;

public interface FullNameDAO {

	public void save(FullName fullname);

	public void update(FullName fullname);

	public void delete(FullName fullname);

	public FullName findById(int id);

	public List<FullName> findByName(String name);

	public List<FullName> findByNameNormalized(String name);

	public List<FullName> findByNameToCompare(String name);

	public List<FullName> findByNameEtype(String name, EType etype);

}
