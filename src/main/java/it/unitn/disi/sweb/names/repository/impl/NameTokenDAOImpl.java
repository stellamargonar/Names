package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.repository.NameTokenDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("nameTokenDAO")
public class NameTokenDAOImpl implements NameTokenDAO {

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public void save(NameToken token) {
		em.merge(token);
	}

	@Override
	@Transactional
	public void update(NameToken token) {
		em.merge(token);
	}

	@Override
	@Transactional
	public void delete(NameToken token) {
		em.remove(token);
	}

	@Override
	@Transactional
	public NameToken findById(int id) {
		return em.find(NameToken.class, id);
	}

	@Override
	@Transactional
	public NameToken findByFullNameIndividualName(FullName fullName,
			IndividualName name) {
		return em
				.createNamedQuery("NameToken.byFullIndividualName",
						NameToken.class).setParameter("fullName", fullName)
				.setParameter("individualName", name).getSingleResult();
	}

	@Override
	@Transactional
	public List<NameToken> findByFullName(FullName fullName) {
		return em.createNamedQuery("NameToken.byFullName", NameToken.class)
				.setParameter("fullName", fullName).getResultList();
	}

	@Override
	@Transactional
	public List<NameToken> findByIndividualName(IndividualName name) {
		return em
				.createNamedQuery("NameToken.byIndividualName", NameToken.class)
				.setParameter("individualName", name).getResultList();
	}

}
