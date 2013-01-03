package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.repository.NameElementDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("nameElementDAO")
public class NameElementDAOImpl implements NameElementDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public NameElement save(NameElement field) {
		NameElement n = em.merge(field);
		em.flush();
		return n;
	}

	@Override
	@Transactional
	public NameElement update(NameElement field) {
		return save(field);
	}

	@Override
	@Transactional
	public void delete(NameElement field) {
		em.remove(field);
		em.flush();
	}

	@Override
	@Transactional
	public NameElement findById(int id) {
		return em.find(NameElement.class, id);
	}

	@Override
	@Transactional
	public List<NameElement> findName(String name) {
		return em
				.createNamedQuery("NameElement.byName", NameElement.class)
				.setParameter("name", name).getResultList();
	}

	@Override
	@Transactional
	public List<NameElement> findAll() {
		return em.createNamedQuery("NameElement.all", NameElement.class)
				.getResultList();
	}

	@Override
	@Transactional
	public List<NameElement> findByEType(EType etype) {
		return em
				.createNamedQuery("NameElement.byEtype", NameElement.class)
				.setParameter("eType", etype).getResultList();
	}

	@Override
	@Transactional
	public NameElement findByNameEType(String name, EType etype) {
		return em
				.createNamedQuery("NameElement.byNameEtype", NameElement.class)
				.setParameter("eType", etype).setParameter("name", name)
				.getSingleResult();
	}

	@Override
	@Transactional
	public void deleteAll() {
		for (NameElement e : findAll()) {
			delete(e);
		}
	}

}
