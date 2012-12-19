package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.repository.ETypeDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("etypeDAO")
public class EtypeDAOImpl implements ETypeDAO {

	@PersistenceContext
	EntityManager em;

	@Override
	
	public void save(EType e) {
		em.merge(e);
	}

	@Override
	@Transactional
	public void update(EType e) {
		em.merge(e);
	}

	@Override
	@Transactional
	public void delete(EType e) {
		em.remove(e);
	}

	@Override
	@Transactional
	public EType findById(int id) {
		return em.find(EType.class, id);
	}

	@Override
	@Transactional
	public List<EType> findAll() {
		return em.createNamedQuery("EType.findAll", EType.class)
				.getResultList();
	}

	@Override
	@Transactional
	public EType findByName(String name) {
		return em.createNamedQuery("EType.findByName", EType.class)
				.setParameter("name", name).getSingleResult();
	}

	@Override
	@Transactional
	public void deleteAll() {
		for (EType e : findAll())
			em.remove(e);
	}

}
