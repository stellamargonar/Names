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

	private EntityManager em;

	@PersistenceContext
	void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public EType save(EType e) {
		EType returned = em.merge(e);
		em.flush();
		return returned;
	}

	@Override
	@Transactional
	public EType update(EType e) {
		return save(e);
	}

	@Override
	@Transactional
	public void delete(EType e) {
		em.remove(e);
		em.flush();
	}

	@Override
	public EType findById(int id) {
		return em.find(EType.class, id);
	}

	@Override
	public List<EType> findAll() {
		return em.createNamedQuery("EType.findAll", EType.class)
				.getResultList();
	}

	@Override
	public EType findByName(String name) {
		return em.createNamedQuery("EType.findByName", EType.class)
				.setParameter("name", name).getSingleResult();
	}

	@Override
	@Transactional
	public void deleteAll() {
		for (EType e : findAll()) {
			em.remove(e);
		}
	}

}
