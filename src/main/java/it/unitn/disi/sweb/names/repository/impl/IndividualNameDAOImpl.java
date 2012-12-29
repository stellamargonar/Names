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

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public void save(IndividualName name) {
		this.em.merge(name);
	}

	@Override
	@Transactional
	public void update(IndividualName name) {
		this.em.merge(name);
	}

	@Override
	@Transactional
	public void delete(IndividualName name) {
		this.em.remove(name);
	}

	@Override
	@Transactional
	public IndividualName findById(int id) {
		return this.em.find(IndividualName.class, id);

	}

	@Override
	@Transactional
	public List<IndividualName> findByName(String name) {
		return this.em
				.createNamedQuery("IndividualName.byName", IndividualName.class)
				.setParameter("name", name).getResultList();
	}

	@Override
	@Transactional
	public List<IndividualName> findByNameEtype(String name, EType etype) {
		return this.em
				.createNamedQuery("IndividualName.byNameEtype",
						IndividualName.class).setParameter("name", name)
				.setParameter("etype", etype).getResultList();
	}

	@Override
	@Transactional
	public boolean isTranslation(String name1, String name2) {
		List<IndividualName> result = this.em
				.createNamedQuery("IndividualName.translation",
						IndividualName.class).setParameter("name1", name1)
				.setParameter("name2", name2).getResultList();

		return result != null && !result.isEmpty() ? true : false;
	}

	@Override
	@Transactional
	public List<IndividualName> findTranslations(IndividualName name) {
		Set<IndividualName> result = new HashSet<>();

		List<IndividualName> list1 = this.em
				.createNamedQuery("IndividualName.alltranslation1",
						IndividualName.class).setParameter("name", name)
				.getResultList();
		result.addAll(list1);

		List<IndividualName> list2 = this.em
				.createNamedQuery("IndividualName.alltranslation2",
						IndividualName.class).setParameter("name", name)
				.getResultList();
		result.addAll(list2);

		return new ArrayList<>(result);
	}

}
