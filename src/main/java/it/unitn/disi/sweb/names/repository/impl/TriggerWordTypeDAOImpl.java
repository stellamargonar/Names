package it.unitn.disi.sweb.names.repository.impl;

import java.util.List;

import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.TriggerWordTypeDAO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("twTypeDAO")
public class TriggerWordTypeDAOImpl implements TriggerWordTypeDAO {

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public void save(TriggerWordType twType) {
		em.merge(twType);
	}

	@Override
	@Transactional
	public void update(TriggerWordType twType) {
		em.merge(twType);
	}

	@Override
	@Transactional
	public void delete(TriggerWordType twType) {
		em.remove(twType);
	}

	@Override
	@Transactional
	public TriggerWordType findById(int id) {
		return em.find(TriggerWordType.class, id);
	}

	@Override
	@Transactional
	public List<TriggerWordType> findAll() {
		return em.createNamedQuery("TWType.findAll", TriggerWordType.class)
				.getResultList();
	}

	@Override
	@Transactional
	public void deleteAll() {
		for (TriggerWordType t: findAll())
			delete(t);
	}

	
	
}
