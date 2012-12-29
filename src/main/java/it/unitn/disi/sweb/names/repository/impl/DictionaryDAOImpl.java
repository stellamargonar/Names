package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.Translation;
import it.unitn.disi.sweb.names.repository.DictionaryDAO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("dictionaryDAO")
public class DictionaryDAOImpl implements DictionaryDAO {

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public Translation create(Translation created) {
		this.em.merge(created);
		return created;

	}

	@Override
	@Transactional
	public Translation getById(int id) {
		return this.em.find(Translation.class, id);
	}

}