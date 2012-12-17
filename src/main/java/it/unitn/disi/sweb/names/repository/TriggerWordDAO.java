package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.TriggerWord;

public interface TriggerWordDAO {
	public void save(TriggerWord triggerWord);

	public void update(TriggerWord triggerWord);

	public void delete(TriggerWord triggerWord);

	public TriggerWord findById(int id);

	public List<TriggerWord> findByTriggerWord(String triggerWord);

	public List<TriggerWord> findVariations(TriggerWord triggerWord);
}
