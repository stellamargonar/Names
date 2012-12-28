package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;

public interface FullNameDAO {

	public FullName save(FullName fullname);

	public FullName update(FullName fullname);

	public void delete(FullName fullname);

	public FullName findById(int id);

	public List<FullName> findByName(String name);

	public List<FullName> findByNameNormalized(String name);

	public List<FullName> findByNameToCompare(String name);

	public List<FullName> findByNameEtype(String name, EType etype);

	public List<FullName> findByEntity(NamedEntity entity);

	public FullName findByEntityName(String name, NamedEntity entity);

	public List<FullName> findVariant(String name, EType etype);

	public List<FullName> findByToken(String token);
	
	public List<FullName> findByNgram(int ngram, int diff);
	
	
}
