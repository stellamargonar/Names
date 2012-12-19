package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.EntityDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("entityDAO")
public class EntityDAOImpl implements EntityDAO {

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public NamedEntity save(NamedEntity entity) {
		NamedEntity result = em.merge(entity);
		em.flush();
		return result;
	}

	@Override
	@Transactional
	public void update(NamedEntity entity) {
		em.merge(entity);
		em.flush();
	}

	@Override
	@Transactional
	public void delete(NamedEntity entity) {
		em.remove(entity);
	}

	@Override
	@Transactional
	public NamedEntity findById(int id) {
		return em.find(NamedEntity.class, id);
	}

	@Override
	@Transactional
	public List<NamedEntity> findByName(String name) {
		List<NamedEntity> resultList = em
				.createNamedQuery("NamedEntity.byName", NamedEntity.class)
				.setParameter("name", name).getResultList();
		return resultList;
	}

}
