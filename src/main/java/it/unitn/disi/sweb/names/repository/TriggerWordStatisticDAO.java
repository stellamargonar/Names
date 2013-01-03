package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordStatistic;

import java.util.List;

public interface TriggerWordStatisticDAO {

	TriggerWordStatistic save(TriggerWordStatistic twetype);

	TriggerWordStatistic update(TriggerWordStatistic twetype);

	void delete(TriggerWordStatistic twetype);

	TriggerWordStatistic findById(int id);

	TriggerWordStatistic findByTriggerWordEtype(TriggerWord triggerWord,
			EType eType);

	List<TriggerWordStatistic> findByTriggerWord(TriggerWord triggerWord);
}
