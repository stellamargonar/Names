package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.repository.UsageStatisticsDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("usageStatisticsDAO")
public class UsageStatisticsDAOImpl implements UsageStatisticsDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public void save(UsageStatistic stat) {
		this.em.merge(stat);
	}

	@Override
	@Transactional
	public void update(UsageStatistic stat) {
		save(stat);
	}

	@Override
	@Transactional
	public void delete(UsageStatistic stat) {
		this.em.remove(stat);
	}

	@Override
	@Transactional
	public UsageStatistic findById(int id) {
		return this.em.find(UsageStatistic.class, id);
	}

	@Override
	@Transactional
	public List<UsageStatistic> findByQuery(String query) {
		return this.em
				.createNamedQuery("UsageStatistic.byQuery",
						UsageStatistic.class).setParameter("query", query)
				.getResultList();
	}

	@Override
	@Transactional
	public UsageStatistic findByQuerySelected(String query, FullName selected) {
		return this.em
				.createNamedQuery("UsageStatistic.byQuerySelected",
						UsageStatistic.class).setParameter("query", query)
				.setParameter("selected", selected).getSingleResult();
	}

}
