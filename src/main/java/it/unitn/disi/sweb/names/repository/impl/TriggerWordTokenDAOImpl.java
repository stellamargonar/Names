package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordToken;
import it.unitn.disi.sweb.names.repository.TriggerWordTokenDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("twTokenDAO")
public class TriggerWordTokenDAOImpl implements TriggerWordTokenDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public TriggerWordToken save(TriggerWordToken twToken) {
		TriggerWordToken t = em.merge(twToken);
		em.flush();
		return t;
	}

	@Override
	@Transactional
	public TriggerWordToken update(TriggerWordToken twToken) {
		return save(twToken);
	}

	@Override
	@Transactional
	public void delete(TriggerWordToken twToken) {
		em.remove(twToken);
		em.flush();
	}

	@Override
	public TriggerWordToken findById(int id) {
		return em.find(TriggerWordToken.class, id);
	}

	@Override
	public List<TriggerWordToken> findByFullName(FullName fullName) {
		return em
				.createNamedQuery("TWToken.byFullName", TriggerWordToken.class)
				.setParameter("name", fullName).getResultList();
	}

	@Override
	public List<TriggerWordToken> findByTriggerWord(TriggerWord triggerWord) {
		return em
				.createNamedQuery("TWToken.byTriggerWord",
						TriggerWordToken.class).setParameter("tw", triggerWord)
				.getResultList();
	}

	@Override
	public List<TriggerWordToken> findByTriggerWordFullName(
			TriggerWord triggerWord, FullName fullName) {
		return em
				.createNamedQuery("TWToken.byTriggerWordFullName",
						TriggerWordToken.class)
				.setParameter("fullname", fullName)
				.setParameter("tw", triggerWord).getResultList();
	}

	@Override
	public TriggerWordToken findFullNamePosition(FullName fullName, int position) {
		return em
				.createNamedQuery("TWToken.byFullNamePosition",
						TriggerWordToken.class).setParameter("name", fullName)
				.setParameter("position", position).getSingleResult();
	}

}
