package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameToken;

import java.util.List;

public interface NameTokenDAO {

	public void save(NameToken token);

	public void update(NameToken token);

	public void delete(NameToken token);

	public NameToken findById(int id);

	public NameToken findByFullNameIndividualName(FullName fullName,
			IndividualName name);

	public List<NameToken> findByFullName(FullName fullName);

	public List<NameToken> findByIndividualName(IndividualName name);


}
