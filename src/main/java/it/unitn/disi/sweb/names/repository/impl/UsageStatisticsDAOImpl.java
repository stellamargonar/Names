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
	public UsageStatistic save(UsageStatistic stat) {
		UsageStatistic u = em.merge(stat);
		em.flush();
		return u;
	}

	@Override
	@Transactional
	public UsageStatistic update(UsageStatistic stat) {
		return save(stat);
	}

	@Override
	@Transactional
	public void delete(UsageStatistic stat) {
		em.remove(stat);
		em.flush();
	}

	@Override
	public UsageStatistic findById(int id) {
		return em.find(UsageStatistic.class, id);
	}

	@Override
	public List<UsageStatistic> findByQuery(String query) {
		return em
				.createNamedQuery("UsageStatistic.byQuery",
						UsageStatistic.class).setParameter("query", query)
				.getResultList();
	}

	@Override
	public UsageStatistic findByQuerySelected(String query, FullName selected) {
		List<UsageStatistic> result = em
				.createNamedQuery("UsageStatistic.byQuerySelected",
						UsageStatistic.class).setParameter("query", query)
				.setParameter("selected", selected).getResultList();
		if (result == null || result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	@Override
	public List<UsageStatistic> findAll() {
		return em.createQuery("from UsageStatistic", UsageStatistic.class)
				.getResultList();
	}

}
