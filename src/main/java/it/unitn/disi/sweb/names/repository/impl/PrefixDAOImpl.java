package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.Prefix;
import it.unitn.disi.sweb.names.repository.PrefixDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

public class PrefixDAOImpl implements PrefixDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}
	@Override
	@Transactional
	public void save(Prefix prefix) {
		this.em.merge(prefix);
	}

	@Override
	@Transactional
	public void update(Prefix prefix) {
		save(prefix);
	}

	@Override
	@Transactional
	public void delete(Prefix prefix) {
		this.em.remove(prefix);
	}

	@Override
	@Transactional
	public Prefix findById(int id) {
		return this.em.find(Prefix.class, id);
	}

	@Override
	@Transactional
	public List<Prefix> findByPrefix(String prefix) {
		return this.em.createNamedQuery("Prefix.byPrefix", Prefix.class)
				.setParameter("prefix", prefix).getResultList();
	}

	@Override
	@Transactional
	public Prefix findByPrefixSelected(String prefix, FullName name) {
		return this.em.createNamedQuery("Prefix.byPrefixSelected", Prefix.class)
				.setParameter("prefix", prefix).setParameter("name", name)
				.getSingleResult();
	}

}
