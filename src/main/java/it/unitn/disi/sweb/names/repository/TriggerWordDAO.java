package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWord;

public interface TriggerWordDAO {
	public TriggerWord save(TriggerWord triggerWord);

	public void update(TriggerWord triggerWord);

	public void delete(TriggerWord triggerWord);

	public TriggerWord findById(int id);

	public List<TriggerWord> findByTriggerWord(String triggerWord);

	public List<TriggerWord> findByTriggerWordEtype(String triggerWord,
			EType etype);

	public List<TriggerWord> findVariations(TriggerWord triggerWord);

	public void deleteAll();
}
