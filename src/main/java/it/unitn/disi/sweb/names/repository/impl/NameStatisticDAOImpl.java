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

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public NameStatistics save(NameStatistics nameStat) {
		NameStatistics s = em.merge(nameStat);
		em.flush();
		return s;
	}
	@Override
	@Transactional
	public NameStatistics update(NameStatistics nameStat) {
		return save(nameStat);
	}

	@Override
	@Transactional
	public void delete(NameStatistics nameStat) {
		em.remove(nameStat);
		em.flush();
	}

	@Override

	public NameStatistics findById(int id) {
		return em.find(NameStatistics.class, id);
	}

	@Override

	public NameStatistics findByNameElement(IndividualName name,
			NameElement element) {
		return em
				.createNamedQuery("NameStatistic.byNameElement",
						NameStatistics.class).setParameter("name", name)
				.setParameter("element", element).getSingleResult();
	}

	@Override

	public List<NameStatistics> findByName(IndividualName name) {
		return em
				.createNamedQuery("NameStatistic.byName", NameStatistics.class)
				.setParameter("name", name).getResultList();
	}

}
