package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordToken;

import java.util.List;

public interface TriggerWordTokenDAO {
	TriggerWordToken save(TriggerWordToken twToken);

	TriggerWordToken upload(TriggerWordToken twToken);

	void delete(TriggerWordToken twToken);

	TriggerWordToken findById(int id);

	List<TriggerWordToken> findByFullName(FullName fullName);

	List<TriggerWordToken> findByTriggerWord(TriggerWord triggerWord);

	TriggerWordToken findByTriggerWordFullName(TriggerWord triggerWord,
			FullName fullName);
}
