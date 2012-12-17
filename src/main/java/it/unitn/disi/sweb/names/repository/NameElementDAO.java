package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;

public interface NameElementDAO {

	public void save(NameElement field);

	public void update(NameElement field);

	public void delete(NameElement field);

	public NameElement findById(int id);

	public List<NameElement> findName(String name);

	public List<NameElement> findAll();

	public List<NameElement> findByEType(EType etype);

	public void deleteAll();
}
