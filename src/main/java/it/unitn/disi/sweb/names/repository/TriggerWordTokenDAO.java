package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordToken;

public interface TriggerWordTokenDAO {

	public void save(TriggerWordToken twToken);

	public void upload(TriggerWordToken twToken);

	public void delete(TriggerWordToken twToken);

	public TriggerWordToken findById(int id);

	public List<TriggerWordToken> findByFullName(FullName fullName);

	public List<TriggerWordToken> findByTriggerWord(TriggerWord triggerWord);

	public TriggerWordToken findByTriggerWordFullName(TriggerWord triggerWord,
			FullName fullName);
}
