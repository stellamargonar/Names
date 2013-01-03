package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameStatistics;

import java.util.List;

public interface NameStatisticDAO {

	NameStatistics save(NameStatistics nameStat);

	NameStatistics update(NameStatistics nameStat);

	void delete(NameStatistics nameStat);

	NameStatistics findById(int id);

	NameStatistics findByNameElement(IndividualName name, NameElement element);

	List<NameStatistics> findByName(IndividualName name);
}
