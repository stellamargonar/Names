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

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public NameToken save(NameToken token) {
		NameToken n = em.merge(token);
		em.flush();
		return n;
	}

	@Override
	@Transactional
	public NameToken update(NameToken token) {
		return save(token);
	}

	@Override
	@Transactional
	public void delete(NameToken token) {
		em.remove(token);
		em.flush();
	}

	@Override
	public NameToken findById(int id) {
		return em.find(NameToken.class, id);
	}

	@Override
	public NameToken findByFullNameIndividualName(FullName fullName,
			IndividualName name) {
		return em
				.createNamedQuery("NameToken.byFullIndividualName",
						NameToken.class).setParameter("fullName", fullName)
				.setParameter("individualName", name).getSingleResult();
	}

	@Override
	public List<NameToken> findByFullName(FullName fullName) {
		return em.createNamedQuery("NameToken.byFullName", NameToken.class)
				.setParameter("fullName", fullName).getResultList();
	}

	@Override
	public List<NameToken> findByIndividualName(IndividualName name) {
		return em
				.createNamedQuery("NameToken.byIndividualName", NameToken.class)
				.setParameter("individualName", name).getResultList();
	}

}
