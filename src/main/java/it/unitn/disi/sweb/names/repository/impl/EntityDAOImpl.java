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
		NamedEntity result = this.em.merge(entity);
		this.em.flush();
		return result;
	}

	@Override
	@Transactional
	public void update(NamedEntity entity) {
		this.em.merge(entity);
		this.em.flush();
	}

	@Override
	@Transactional
	public void delete(NamedEntity entity) {
		this.em.remove(entity);
	}

	@Override
	@Transactional
	public NamedEntity findById(int id) {
		return this.em.find(NamedEntity.class, id);
	}

	@Override
	@Transactional
	public List<NamedEntity> findByName(String name) {
		List<NamedEntity> resultList = this.em
				.createNamedQuery("NamedEntity.byName", NamedEntity.class)
				.setParameter("name", name).getResultList();
		return resultList;
	}

	@Override
	@Transactional
	public List<NamedEntity> findByNameEtype(String name, EType etype) {
		List<NamedEntity> resultList = this.em
				.createNamedQuery("NamedEntity.byNameEtype", NamedEntity.class)
				.setParameter("name", name).setParameter("etype", etype)
				.getResultList();
		return resultList;

	}

	@Override
	@Transactional
	public List<NamedEntity> findByUrl(String url) {
		return this.em
				.createNamedQuery("NamedEntity.byUrl",
						NamedEntity.class)
				.setParameter("url", url).getResultList();
	}

	@Override
	@Transactional
	public List<NamedEntity> findByNameUrl(String name,
			String url) {
		return this.em
				.createNamedQuery("NamedEntity.byNameUrl",
						NamedEntity.class)
				.setParameter("url", url)
				.setParameter("name", name).getResultList();
	}

	@Override
	@Transactional
	public List<NamedEntity> findByEtype(EType etype) {
		return this.em
				.createNamedQuery("NamedEntity.byEType",
						NamedEntity.class)
				.setParameter("etype", etype).getResultList();
	}

}
