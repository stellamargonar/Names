package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.Prefix;
import it.unitn.disi.sweb.names.repository.PrefixDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("prefixDAO")
public class PrefixDAOImpl implements PrefixDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}
	@Override
	@Transactional
	public Prefix save(Prefix prefix) {
		Prefix p = em.merge(prefix);
		em.flush();
		return p;
	}

	@Override
	@Transactional
	public Prefix update(Prefix prefix) {
		return save(prefix);
	}
	@Override
	@Transactional
	public void delete(Prefix prefix) {
		em.remove(prefix);
		em.flush();
	}

	@Override
	@Transactional
	public Prefix findById(int id) {
		return em.find(Prefix.class, id);
	}

	@Override
	@Transactional
	public List<Prefix> findByPrefix(String prefix) {
		return em.createNamedQuery("Prefix.byPrefix", Prefix.class)
				.setParameter("prefix", prefix).getResultList();
	}

	@Override
	@Transactional
	public Prefix findByPrefixSelected(String prefix, FullName name) {
		List<Prefix> result = em
				.createNamedQuery("Prefix.byPrefixSelected", Prefix.class)
				.setParameter("prefix", prefix).setParameter("name", name)
				.getResultList();
		if (result == null || result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}

}
