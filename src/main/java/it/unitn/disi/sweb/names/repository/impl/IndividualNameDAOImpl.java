package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.repository.IndividualNameDAO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("individualNameDAO")
public class IndividualNameDAOImpl implements IndividualNameDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public IndividualName save(IndividualName name) {
		IndividualName n = em.merge(name);
		em.flush();
		return n;
	}

	@Override
	@Transactional
	public IndividualName update(IndividualName name) {
		return save(name);
	}

	@Override
	@Transactional
	public void delete(IndividualName name) {
		em.remove(name);
		em.flush();
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
				.createNamedQuery("IndividualName.byNameEtype",
						IndividualName.class).setParameter("name", name)
				.setParameter("etype", etype).getResultList();
	}

	@Override
	@Transactional
	public boolean isTranslation(String name1, String name2) {
		List<IndividualName> result = em
				.createNamedQuery("IndividualName.translation",
						IndividualName.class).setParameter("name1", name1)
				.setParameter("name2", name2).getResultList();

		return result != null && !result.isEmpty() ? true : false;
	}

	@Override
	@Transactional
	public List<IndividualName> findTranslations(IndividualName name) {
		Set<IndividualName> result = new HashSet<>();

		List<IndividualName> list1 = em
				.createNamedQuery("IndividualName.alltranslation1",
						IndividualName.class)
				.setParameter("name", name.getName().toLowerCase()).getResultList();
		result.addAll(list1);

		List<IndividualName> list2 = em
				.createNamedQuery("IndividualName.alltranslation2",
						IndividualName.class)
				.setParameter("name", name.getName().toLowerCase()).getResultList();
		result.addAll(list2);

		return new ArrayList<>(result);
	}

	@Override
	public List<IndividualName> findByNGram(int ngram, int diff) {
		return em
				.createNamedQuery("IndividualName.byNgram",
						IndividualName.class).setParameter("ngram", ngram)
				.setParameter("diff", diff).getResultList();
	}

}
