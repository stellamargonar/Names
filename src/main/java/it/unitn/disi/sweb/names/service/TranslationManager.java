package it.unitn.disi.sweb.names.service;

import java.util.List;
/**
 * interface for managing and retrieving translation for names analyzed in the
 * system.
 *
 * Translations of names are retrieve through different ways:
 * <ul>
 * <li>behind the name website</li>
 * <li>Heiner Database (stored locally)</li>
 * </ul>
 *
 * @author stella margonar #LINKTESI# #LINKTESIENRICO#
 *
 */
public interface TranslationManager {

	/**
	 * search in the datasources for (exact) translations of the name
	 * represented by the string in inpu
	 *
	 * @param name
	 *            name to be searched
	 * @return list of translations, null if no translation available
	 */
	List<String> findTranslation(String name);
}
