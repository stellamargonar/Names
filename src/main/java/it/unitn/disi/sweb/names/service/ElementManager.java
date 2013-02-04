package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.IndividualName;
import it.unitn.disi.sweb.names.model.NameElement;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordType;

import java.util.List;

public interface ElementManager {

	/**
	 * creates a new trigger word with name and type given in input
	 * @param triggerWord
	 * @param type
	 * @return
	 */
	TriggerWord createTriggerWord(String triggerWord, TriggerWordType type);

	List<NameElement> findNameElement(EType etype);
	NameElement findNameElement(String element, EType etype);

	List<TriggerWordType> findTriggerWordType(EType etype);
	TriggerWordType findTriggerWordType(String type, EType etype);

	/**
	 * search for Indivual name or trigger word, which match the input string
	 * wrt misspellings
	 *
	 * @param s input token
	 * @return
	 */
	List<Object> findMisspellings(String s);

	/**
	 * finds instances of fullName which contain the individual name passed as input
	 * @param name individual name (stored in the database)
	 * @return fullnames that contain name
	 */
	List<FullName> find(IndividualName name);

	/**
	 * finds instances of fullName which contain the trigger word passed as input
	 * @param triggerWord  (stored in the database)
	 * @return fullnames that contain triggerWord
	 */
	List<FullName> find(TriggerWord triggerWord);

	List<TriggerWord> findTriggerWord(String t);

	TriggerWord findTriggerWord(String t, TriggerWordType type);

	int frequency(String token, NameElement el);
}
