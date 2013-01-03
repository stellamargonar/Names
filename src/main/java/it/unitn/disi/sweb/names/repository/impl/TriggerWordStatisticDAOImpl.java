package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordStatistic;
import it.unitn.disi.sweb.names.repository.TriggerWordStatisticDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("twStatisticDAO")
public class TriggerWordStatisticDAOImpl implements TriggerWordStatisticDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public TriggerWordStatistic save(TriggerWordStatistic twetype) {
		TriggerWordStatistic t = em.merge(twetype);
		em.flush();
		return t;
	}

	@Override
	@Transactional
	public TriggerWordStatistic update(TriggerWordStatistic twetype) {
		return save(twetype);
	}

	@Override
	@Transactional
	public void delete(TriggerWordStatistic twetype) {
		em.remove(twetype);
		em.flush();
	}

	@Override
	@Transactional
	public TriggerWordStatistic findById(int id) {
		return em.find(TriggerWordStatistic.class, id);
	}

	@Override
	@Transactional
	public TriggerWordStatistic findByTriggerWordEtype(TriggerWord triggerWord,
			EType eType) {
		return em
				.createNamedQuery("TWStatistic.byTriggerWordEtype",
						TriggerWordStatistic.class)
				.setParameter("tw", triggerWord).setParameter("etype", eType)
				.getSingleResult();
	}

	@Override
	@Transactional
	public List<TriggerWordStatistic> findByTriggerWord(TriggerWord triggerWord) {
		return em
				.createNamedQuery("TWStatistic.byTriggerWord",
						TriggerWordStatistic.class)
				.setParameter("tw", triggerWord).getResultList();
	}

}
