package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameToken;

import java.util.List;

public interface NameTokenDAO {

	void save(NameToken token);

	void update(NameToken token);

	void delete(NameToken token);

	NameToken findById(int id);

	NameToken findByFullNameIndividualName(FullName fullName,
			IndividualName name);

	List<NameToken> findByFullName(FullName fullName);

	List<NameToken> findByIndividualName(IndividualName name);

}