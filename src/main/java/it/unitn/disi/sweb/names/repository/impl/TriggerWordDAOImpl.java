package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("triggerWordDAO")
public class TriggerWordDAOImpl implements TriggerWordDAO {

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public TriggerWord save(TriggerWord triggerWord) {
		return em.merge(triggerWord);
	}

	@Override
	@Transactional
	public void update(TriggerWord triggerWord) {
		em.merge(triggerWord);
	}

	@Override
	@Transactional
	public void delete(TriggerWord triggerWord) {
		em.remove(triggerWord);
	}

	@Override
	@Transactional
	public TriggerWord findById(int id) {
		return em.find(TriggerWord.class, id);
	}

	@Override
	@Transactional
	public List<TriggerWord> findByTriggerWord(String triggerWord) {
		return em.createNamedQuery("TriggerWord.byTW", TriggerWord.class)
				.setParameter("tw", triggerWord).getResultList();
	}

	@Override
	@Transactional
	public List<TriggerWord> findVariations(TriggerWord triggerWord) {
		return em
				.createNamedQuery("TriggerWord.variationsByTW",
						TriggerWord.class).setParameter("tw", triggerWord)
				.getResultList();
	}

	@Override
	@Transactional
	public void deleteAll() {
		for (TriggerWord t : em.createQuery("from TriggerWord",
				TriggerWord.class).getResultList())
			delete(t);
	}

	@Override
	@Transactional
	public List<TriggerWord> findByTriggerWordEtype(String triggerWord,
			EType etype) {
		return em.createNamedQuery("TriggerWord.byTWEtype", TriggerWord.class)
				.setParameter("tw", triggerWord).setParameter("etype", etype)
				.getResultList();
	}
}
