package it.unitn.disi.sweb.names.repository.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;
import it.unitn.disi.sweb.names.repository.EntityDAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("entityDAO")
public class EntityDAOImpl implements EntityDAO {

	private EntityManager em;

	@PersistenceContext
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public NamedEntity save(NamedEntity entity) {
		NamedEntity result = em.merge(entity);
		em.flush();
		return result;
	}

	@Override
	@Transactional
	public NamedEntity update(NamedEntity entity) {
		return save(entity);
	}

	@Override
	@Transactional
	public void delete(NamedEntity entity) {
		em.remove(entity);
		em.flush();
	}

	@Override
	@Transactional
	public NamedEntity findById(int id) {
		return em.find(NamedEntity.class, id);
	}

	@Override
	@Transactional
	public List<NamedEntity> findByName(String name) {
		List<NamedEntity> resultList = em
				.createNamedQuery("NamedEntity.byName", NamedEntity.class)
				.setParameter("name", name).getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public List<NamedEntity> findByNameEtype(String name, EType etype) {
		List<NamedEntity> resultList = em
				.createNamedQuery("NamedEntity.byNameEtype", NamedEntity.class)
				.setParameter("name", name).setParameter("etype", etype)
				.getResultList();
		return resultList;

	}

	@Override
	@Transactional
	public List<NamedEntity> findByUrl(String url) {
		return em
				.createNamedQuery("NamedEntity.byUrl",
						NamedEntity.class)
				.setParameter("url", url).getResultList();
	}

	@Override
	@Transactional
	public List<NamedEntity> findByNameUrl(String name,
			String url) {
		return em
				.createNamedQuery("NamedEntity.byNameUrl",
						NamedEntity.class)
				.setParameter("url", url)
				.setParameter("name", name).getResultList();
	}

	@Override
	@Transactional
	public List<NamedEntity> findByEtype(EType etype) {
		return em
				.createNamedQuery("NamedEntity.byEType",
						NamedEntity.class)
				.setParameter("etype", etype).getResultList();
	}

}
