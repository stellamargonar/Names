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

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public void save(TriggerWordStatistic twetype) {
		this.em.merge(twetype);
	}

	@Override
	@Transactional
	public void update(TriggerWordStatistic twetype) {
		this.em.merge(twetype);
	}

	@Override
	@Transactional
	public void delete(TriggerWordStatistic twetype) {
		this.em.remove(twetype);
	}

	@Override
	@Transactional
	public TriggerWordStatistic findById(int id) {
		return this.em.find(TriggerWordStatistic.class, id);
	}

	@Override
	@Transactional
	public TriggerWordStatistic findByTriggerWordEtype(TriggerWord triggerWord,
			EType eType) {
		return this.em
				.createNamedQuery("TWStatistic.byTriggerWordEtype",
						TriggerWordStatistic.class).setParameter("tw", triggerWord)
				.setParameter("etype", eType).getSingleResult();
	}

	@Override
	@Transactional
	public List<TriggerWordStatistic> findByTriggerWord(TriggerWord triggerWord) {
		return this.em
				.createNamedQuery("TWStatistic.byTriggerWord",
						TriggerWordStatistic.class).setParameter("tw", triggerWord)
				.getResultList();
	}

}
