package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.Prefix;

import java.util.List;

public interface PrefixDAO {

	void save(Prefix prefix);

	void update(Prefix prefix);

	void delete(Prefix prefix);

	Prefix findById(int id);

	List<Prefix> findByPrefix(String prefix);

	Prefix findByPrefixSelected(String prefix, FullName name);

}
