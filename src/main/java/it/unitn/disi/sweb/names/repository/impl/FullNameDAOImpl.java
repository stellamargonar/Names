package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.FullNameDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("fullNameDAO")
public class FullNameDAOImpl implements FullNameDAO {

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public FullName save(FullName fullname) {
		FullName result = em.merge(fullname);
		em.flush();
		return result;
	}

	@Override
	@Transactional
	public FullName update(FullName fullname) {
		FullName result = em.merge(fullname);
		em.flush();
		return result;
	}

	@Override
	@Transactional
	public void delete(FullName fullname) {
		em.remove(fullname);
	}

	@Override
	@Transactional
	public FullName findById(int id) {
		return em.find(FullName.class, id);
	}

	@Override
	@Transactional
	public List<FullName> findByName(String name) {
		return em.createNamedQuery("FullName.byName", FullName.class)
				.setParameter("name", name).getResultList();
	}

	@Override
	@Transactional
	public List<FullName> findByNameNormalized(String name) {
		return em.createNamedQuery("FullName.byNameNormalized", FullName.class)
				.setParameter("nameNormalized", name).getResultList();
	}

	@Override
	@Transactional
	public List<FullName> findByNameToCompare(String name) {
		return em.createNamedQuery("FullName.byNameToCompare", FullName.class)
				.setParameter("nameToCompare", name).getResultList();
	}

	@Override
	@Transactional
	public List<FullName> findByNameEtype(String name, EType etype) {
		return em.createNamedQuery("FullName.byNameEtype", FullName.class)
				.setParameter("name", name).setParameter("etype", etype)
				.getResultList();
	}

	@Override
	@Transactional
	public List<FullName> findByEntity(NamedEntity entity) {
		return em.createNamedQuery("FullName.byEntity", FullName.class)
				.setParameter("entity", entity).getResultList();
	}

	@Override
	@Transactional
	public FullName findByEntityName(String name, NamedEntity entity) {
		List<FullName> result = em
				.createNamedQuery("FullName.byEntityName", FullName.class)
				.setParameter("name", name).setParameter("entity", entity)
				.getResultList();
		if (result == null)
			return null;
		if (result.size() > 0)
			return result.get(0);
		else
			return null;
	}

}
