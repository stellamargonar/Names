package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.EType;

public interface ETypeDAO {

	public void save(EType e);

	public void update(EType e);

	public void delete(EType e);

	public EType findById(int id);

	public EType findByName(String name);
	
	public List<EType> findAll();
	
	public void deleteAll();
}
