package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWordType;
import it.unitn.disi.sweb.names.repository.TriggerWordTypeDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("twTypeDAO")
public class TriggerWordTypeDAOImpl implements TriggerWordTypeDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public TriggerWordType save(TriggerWordType twType) {
		TriggerWordType t = em.merge(twType);
		em.flush();
		return t;
	}

	@Override
	@Transactional
	public TriggerWordType update(TriggerWordType twType) {
		return save(twType);
	}

	@Override
	@Transactional
	public void delete(TriggerWordType twType) {
		em.remove(twType);
		em.flush();
	}

	@Override
	public TriggerWordType findById(int id) {
		return em.find(TriggerWordType.class, id);
	}

	@Override
	public List<TriggerWordType> findAll() {
		return em.createNamedQuery("TWType.findAll", TriggerWordType.class)
				.getResultList();
	}

	@Override
	@Transactional
	public void deleteAll() {
		for (TriggerWordType t : findAll()) {
			delete(t);
		}
	}

	@Override
	public List<TriggerWordType> findByName(String name) {
		return em.createNamedQuery("TWType.findByName", TriggerWordType.class)
				.setParameter("name", name).getResultList();
	}

	@Override
	public List<TriggerWordType> findByEType(EType etype) {
		return em.createNamedQuery("TWType.findByEtype", TriggerWordType.class)
				.setParameter("etype", etype).getResultList();
	}

	@Override
	public TriggerWordType findByNameEType(String name, EType etype) {
		return em
				.createNamedQuery("TWType.findByNameEtype",
						TriggerWordType.class).setParameter("etype", etype)
				.setParameter("name", name).getSingleResult();
	}

}
