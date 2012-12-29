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
		return update(fullname);
	}

	@Override
	@Transactional
	public FullName update(FullName fullname) {
		FullName result = this.em.merge(fullname);
		this.em.flush();
		return result;
	}

	@Override
	@Transactional
	public void delete(FullName fullname) {
		this.em.remove(fullname);
	}

	@Override
	@Transactional
	public FullName findById(int id) {
		return this.em.find(FullName.class, id);
	}

	@Override
	@Transactional
	public List<FullName> findByName(String name) {
		return this.em.createNamedQuery("FullName.byName", FullName.class)
				.setParameter("name", name).getResultList();
	}

	@Override
	@Transactional
	public List<FullName> findByNameNormalized(String name) {
		return this.em.createNamedQuery("FullName.byNameNormalized", FullName.class)
				.setParameter("nameNormalized", name).getResultList();
	}

	@Override
	@Transactional
	public List<FullName> findByNameToCompare(String name) {
		return this.em.createNamedQuery("FullName.byNameToCompare", FullName.class)
				.setParameter("nameToCompare", name).getResultList();
	}

	@Override
	@Transactional
	public List<FullName> findByNameEtype(String name, EType etype) {
		return this.em.createNamedQuery("FullName.byNameEtype", FullName.class)
				.setParameter("name", name).setParameter("etype", etype)
				.getResultList();
	}

	@Override
	@Transactional
	public List<FullName> findByEntity(NamedEntity entity) {
		return this.em.createNamedQuery("FullName.byEntity", FullName.class)
				.setParameter("entity", entity).getResultList();
	}

	@Override
	@Transactional
	public FullName findByEntityName(String name, NamedEntity entity) {
		List<FullName> result = this.em
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
	@Transactional
	public List<FullName> findVariant(String name, EType etype) {
		return this.em.createNamedQuery("FullName.variantForName", FullName.class)
				.setParameter("name", name).setParameter("etype", etype)
				.getResultList();
	}

	@Override
	@Transactional
	public List<FullName> findByToken(String token) {
		return this.em.createNamedQuery("FullName.byToken", FullName.class)
				.setParameter("name", token).getResultList();
	}

	@Override
	public List<FullName> findByNgram(int ngram, int diff) {
		return this.em.createNamedQuery("FullName.byNgram", FullName.class)
				.setParameter("code", ngram).setParameter("diff", diff).getResultList();
	}
}
