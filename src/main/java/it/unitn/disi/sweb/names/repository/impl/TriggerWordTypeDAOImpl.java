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
	public void save(TriggerWordType twType) {
		this.em.merge(twType);
	}

	@Override
	@Transactional
	public void update(TriggerWordType twType) {
		this.em.merge(twType);
	}

	@Override
	@Transactional
	public void delete(TriggerWordType twType) {
		this.em.remove(twType);
	}

	@Override
	@Transactional
	public TriggerWordType findById(int id) {
		return this.em.find(TriggerWordType.class, id);
	}

	@Override
	@Transactional
	public List<TriggerWordType> findAll() {
		return this.em.createNamedQuery("TWType.findAll", TriggerWordType.class)
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
	@Transactional
	public TriggerWordType findByName(String name) {
		return this.em.createNamedQuery("TWType.findByName", TriggerWordType.class)
				.setParameter("name", name).getSingleResult();
	}

	@Override
	public List<TriggerWordType> findByEType(EType etype) {
		return this.em.createNamedQuery("TWType.findByEtype", TriggerWordType.class)
				.setParameter("etype", etype).getResultList();
	}

}
