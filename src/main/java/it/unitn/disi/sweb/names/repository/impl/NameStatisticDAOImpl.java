package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.NameStatistics;
import it.unitn.disi.sweb.names.repository.NameStatisticDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("nameStatisticDAO")
public class NameStatisticDAOImpl implements NameStatisticDAO {

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public void save(NameStatistics nameStat) {
		this.em.merge(nameStat);
	}
	@Override
	public void update(NameStatistics nameStat) {
		save(nameStat);
	}

	@Override
	public void delete(NameStatistics nameStat) {
		this.em.remove(nameStat);
	}

	@Override
	public NameStatistics findById(int id) {
		return this.em.find(NameStatistics.class, id);
	}

	@Override
	public NameStatistics findByNameElement(IndividualName name,
			NameElement element) {
		return this.em
				.createNamedQuery("NameStatistics.byNameElement",
						NameStatistics.class).setParameter("name", name)
				.setParameter("element", element).getSingleResult();
	}

	@Override
	public List<NameStatistics> findByName(IndividualName name) {
		return this.em
				.createNamedQuery("NameStatistics.byName", NameStatistics.class)
				.setParameter("name", name).getResultList();
	}

}
