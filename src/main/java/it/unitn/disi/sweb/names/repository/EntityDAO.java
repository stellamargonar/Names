package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;

/**
 * interface for data access operation for {@link NamedEntity} objects
 *
 * @author stella margonar #LINKTESI# #LINKTESIENRICO#
 */
public interface EntityDAO {

	/**
	 * persists an instance of NamedEntity in the database
	 *
	 * @param entity
	 *            instance to persist
	 * @return the persisted instance
	 */
	NamedEntity save(NamedEntity entity);

	/**
	 * updates the entity in the database. If there is no corresponding entity,
	 * then it creates a new one
	 *
	 * @param entity
	 *            instance to be updated
	 * @return the updated instance
	 */
	NamedEntity update(NamedEntity entity);

	/**
	 * removes from the database the entry corresponding to the input instance
	 *
	 * @param entity
	 *            object to be removed
	 */
	void delete(NamedEntity entity);

	/**
	 * retrieves the instance of the object identified by the input parameter.
	 * If none, then returns null
	 *
	 * @param id
	 *            identifier
	 * @return object with identifier = id, null if no corresponding object
	 *         exists
	 */
	NamedEntity findById(int id);

	/**
	 * search for entities which have a full name equals to the input string
	 *
	 * @param name
	 *            string representing the name to be searched
	 * @return entities with at least one name equals to name
	 */
	List<NamedEntity> findByName(String name);

	/**
	 * search for entities which have a full name equals to the input string,
	 * and the same etype
	 *
	 * @param name
	 *            string representing the name to be searched
	 * @param etype
	 * @return entities with at least one name equals to name
	 */
	List<NamedEntity> findByNameEtype(String name, EType etype);

	/**
	 * search for entities with description url equals to the input parameter
	 *
	 * @param url
	 *            url or description of the entity
	 * @return list of named entities which description is equals to url
	 */
	List<NamedEntity> findByUrl(String url);

	/**
	 * search for entities which have a full name equals to the input string,
	 * and description equals to the input url
	 *
	 * @param name
	 *            string representing the name to be searched
	 * @param url
	 *            description of the entity
	 * @return entities with at least one name equals to name and description
	 *         equals to url
	 */
	List<NamedEntity> findByNameUrl(String name, String url);

	/**
	 * search for all the entities of the given etype
	 *
	 * @param etype
	 * @return
	 */
	List<NamedEntity> findByEtype(EType etype);

}