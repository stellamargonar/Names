package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;

import java.util.List;

/**
 * interface for data access operation for {@link EType} objects
 *
 * @author stella margonar #LINKTESI# #LINKTESIENRICO#
 *
 */
public interface ETypeDAO {

	/**
	 * persists an instance of EType in the database
	 *
	 * @param entity
	 *            instance to persist
	 * @return the persisted instance
	 */
	EType save(EType e);

	/**
	 * updates the entity in the database. If there is no corresponding entity,
	 * then it creates a new one
	 *
	 * @param entity
	 *            instance to be updated
	 * @return the updated instance
	 */
	EType update(EType e);

	/**
	 * removes from the database the entry corresponding to the input instance
	 *
	 * @param entity
	 *            object to be removed
	 */
	void delete(EType e);

	/**
	 * retrieves the instance of the object identified by the input parameter.
	 * If none, then returns null
	 *
	 * @param id
	 *            identifier
	 * @return object with identifier = id, null if no corresponding object
	 *         exists
	 */
	EType findById(int id);

	/**
	 * search for the etype object with name as input (lower case)
	 *
	 * @param name
	 *            etype name, lowercase
	 * @return the corresponding EType instance if any, null otherwise
	 */
	EType findByName(String name);

	/**
	 * retrieves all the stored etypes
	 *
	 * @return list of etypes
	 */
	List<EType> findAll();

	/**
	 * delete from the system all the etypes
	 */
	void deleteAll();
}