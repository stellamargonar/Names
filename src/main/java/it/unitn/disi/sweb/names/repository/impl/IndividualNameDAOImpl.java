package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.repository.IndividualNameDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("individualNameDAO")
public class IndividualNameDAOImpl implements IndividualNameDAO {

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public void save(IndividualName name) {
		em.merge(name);
	}

	@Override
	@Transactional
	public void update(IndividualName name) {
		em.merge(name);
	}

	@Override
	@Transactional
	public void delete(IndividualName name) {
		em.remove(name);
	}

	@Override
	@Transactional
	public IndividualName findById(int id) {
		return em.find(IndividualName.class, id);

	}

	@Override
	@Transactional
	public List<IndividualName> findByName(String name) {
		return em
				.createNamedQuery("IndividualName.byName", IndividualName.class)
				.setParameter("name", name).getResultList();
	}

	@Override
	@Transactional
	public List<IndividualName> findByNameEtype(String name, EType etype) {
		return em
				.createNamedQuery("IndividualName.byNameEtype", IndividualName.class)
				.setParameter("name", name).setParameter("etype", etype)
				.getResultList();
	}

}
