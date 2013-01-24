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

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

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
		return save(fullname);
	}

	@Override
	@Transactional
	public void delete(FullName fullname) {
		em.remove(fullname);
		em.flush();
	}

	@Override
	public FullName findById(int id) {
		return em.find(FullName.class, id);
	}

	@Override
	public List<FullName> findByName(String name) {
		return em.createNamedQuery("FullName.byName", FullName.class)
				.setParameter("name", name).getResultList();
	}

	@Override
	public List<FullName> findByNameNormalized(String name) {
		return em.createNamedQuery("FullName.byNameNormalized", FullName.class)
				.setParameter("nameNormalized", name).getResultList();
	}

	@Override
	public List<FullName> findByNameToCompare(String name) {
		return em.createNamedQuery("FullName.byNameToCompare", FullName.class)
				.setParameter("nameToCompare", name).getResultList();
	}

	@Override
	public List<FullName> findByNameEtype(String name, EType etype) {
		return em.createNamedQuery("FullName.byNameEtype", FullName.class)
				.setParameter("name", name).setParameter("etype", etype)
				.getResultList();
	}

	@Override
	public List<FullName> findByEntity(NamedEntity entity) {
		return em.createNamedQuery("FullName.byEntity", FullName.class)
				.setParameter("entity", entity).getResultList();
	}

	@Override
	public FullName findByEntityName(String name, NamedEntity entity) {
		List<FullName> result = em
				.createNamedQuery("FullName.byEntityName", FullName.class)
				.setParameter("name", name).setParameter("entity", entity)
				.getResultList();
		if (result == null) {
			return null;
		}
		if (result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<FullName> findVariant(String name, EType etype) {

		if (etype != null) {
			return em
					.createNamedQuery("FullName.variantForName", FullName.class)
					.setParameter("name", name).setParameter("etype", etype)
					.getResultList();
		} else {
			return em
					.createNamedQuery("FullName.variantForNameNoEtype",
							FullName.class).setParameter("name", name)
					.getResultList();
		}
	}
	@Override
	public List<FullName> findByToken(String token) {
		return em.createNamedQuery("FullName.byToken", FullName.class)
				.setParameter("name", token).getResultList();
	}

	@Override
	public List<FullName> findByNgram(int ngram, int diff) {
		return em.createNamedQuery("FullName.byNgram", FullName.class)
				.setParameter("code", ngram).setParameter("diff", diff)
				.getResultList();
	}
}
