package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.Translation;
import it.unitn.disi.sweb.names.repository.DictionaryDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("dictionaryDAO")
public class DictionaryDAOImpl implements DictionaryDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public Translation create(Translation created) {
		em.merge(created);
		return created;

	}

	@Override
	@Transactional
	public Translation getById(int id) {
		return em.find(Translation.class, id);
	}

	@Override
	public List<String> findTranslations(String name) {
		List<String> list1 = em
				.createNamedQuery("Translation.find1", String.class)
				.setParameter("name", name).getResultList();
		List<String> list2 = em
				.createNamedQuery("Translation.find2", String.class)
				.setParameter("name", name).getResultList();
		list1.addAll(list2);
		return list1;
	}

	public boolean contains(String source, String target) {
		return !em.createNamedQuery("Translation.exists", Translation.class)
				.setParameter("name1", source).setParameter("name2", target)
				.getResultList().isEmpty();
	}
}