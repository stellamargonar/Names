package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.repository.TriggerWordDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("triggerWordDAO")
public class TriggerWordDAOImpl implements TriggerWordDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public TriggerWord save(TriggerWord triggerWord) {
		TriggerWord t = em.merge(triggerWord);
		em.flush();
		return t;
	}

	@Override
	@Transactional
	public TriggerWord update(TriggerWord triggerWord) {
		return save(triggerWord);
	}

	@Override
	@Transactional
	public void delete(TriggerWord triggerWord) {
		em.remove(triggerWord);
		em.flush();
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

		Set<TriggerWord> result = em
				.createNamedQuery("TriggerWord.variationsByTW",
						TriggerWord.class)
				.setParameter("tw", triggerWord.getTriggerWord())
				.setParameter("type", triggerWord.getType()).getSingleResult()
				.getVariations();
		return new ArrayList<>(result);
	}

	@Override
	@Transactional
	public void deleteAll() {
		for (TriggerWord t : em.createQuery("from TriggerWord",
				TriggerWord.class).getResultList()) {
			delete(t);
		}
	}

	@Override
	@Transactional
	public List<TriggerWord> findByTriggerWordEtype(String triggerWord,
			EType etype) {
		return em.createNamedQuery("TriggerWord.byTWEtype", TriggerWord.class)
				.setParameter("tw", triggerWord).setParameter("etype", etype)
				.getResultList();
	}

	@Override
	@Transactional
	public boolean isVariation(String t1, String t2) {
		List<TriggerWord> result = em
				.createNamedQuery("TriggerWord.isVariations", TriggerWord.class)
				.setParameter("t1", t1).setParameter("t2", t2).getResultList();
		return result != null && !result.isEmpty() ? true : false;
	}

	@Override
	public List<TriggerWord> findByNGram(int ngram, int diff) {
		return em
				.createNamedQuery("TriggerWord.byNgram",
						TriggerWord.class).setParameter("ngram", ngram)
				.setParameter("diff", diff).getResultList();
	}
}
