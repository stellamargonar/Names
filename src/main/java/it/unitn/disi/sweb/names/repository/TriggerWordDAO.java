package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWord;

import java.util.List;

public interface TriggerWordDAO {
	TriggerWord save(TriggerWord triggerWord);

	TriggerWord update(TriggerWord triggerWord);

	void delete(TriggerWord triggerWord);

	TriggerWord findById(int id);

	List<TriggerWord> findByTriggerWord(String triggerWord);

	List<TriggerWord> findByTriggerWordEtype(String triggerWord, EType etype);

	List<TriggerWord> findVariations(TriggerWord triggerWord);

	boolean isVariation(String t1, String t2);

	void deleteAll();
}