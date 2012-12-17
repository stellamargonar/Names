package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.IndividualName;

public interface IndividualNameDAO {

	public void save(IndividualName name);

	public void update(IndividualName name);

	public void delete(IndividualName name);

	public IndividualName findById(int id);

	public List<IndividualName> findByName(String name);

}
