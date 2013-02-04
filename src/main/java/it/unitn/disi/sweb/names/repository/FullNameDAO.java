package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.NamedEntity;

import java.util.List;

/**
 * interface for data access operation for {@link FullName} objects
 *
 * @author stella margonar #LINKTESI# #LINKTESIENRICO#
 *
 */
public interface FullNameDAO {

	/**
	 * persists an instance of FullName in the database
	 *
	 * @param entity
	 *            instance to persist
	 * @return the persisted instance
	 */
	FullName save(FullName fullname);

	/**
	 * updates the entity in the database. If there is no corresponding entity,
	 * then it creates a new one
	 *
	 * @param entity
	 *            instance to be updated
	 * @return the updated instance
	 */
	FullName update(FullName fullname);

	/**
	 * removes from the database the entry corresponding to the input instance
	 *
	 * @param entity
	 *            object to be removed
	 */
	void delete(FullName fullname);

	/**
	 * retrieves the instance of the object identified by the input parameter.
	 * If none, then returns null
	 *
	 * @param id
	 *            identifier
	 * @return object with identifier = id, null if no corresponding object
	 *         exists
	 */
	FullName findById(int id);

	List<FullName> findByName(String name);

	List<FullName> findByNameNormalized(String name);

	List<FullName> findByNameToCompare(String name);

	List<FullName> findByNameEtype(String name, EType etype);

	List<FullName> findByEntity(NamedEntity entity);

	FullName findByEntityName(String name, NamedEntity entity);

	List<FullName> findVariant(String name, EType etype);

	List<FullName> findByToken(String token);

	List<FullName> findByNgram(int ngram, int diff);


}