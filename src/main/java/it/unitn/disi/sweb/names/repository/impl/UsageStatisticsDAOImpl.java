package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.UsageStatistic;
import it.unitn.disi.sweb.names.repository.UsageStatisticsDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

@Component("usageStatisticsDAO")
public class UsageStatisticsDAOImpl implements UsageStatisticsDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	public void save(UsageStatistic stat) {
		this.em.merge(stat);
	}

	@Override
	public void update(UsageStatistic stat) {
		save(stat);
	}

	@Override
	public void delete(UsageStatistic stat) {
		this.em.remove(stat);
	}

	@Override
	public UsageStatistic findById(int id) {
		return this.em.find(UsageStatistic.class, id);
	}

	@Override
	public List<UsageStatistic> findByQuery(String query) {
		return this.em
				.createNamedQuery("UsageStatistic.byQuery",
						UsageStatistic.class).setParameter("query", query)
				.getResultList();
	}

}
