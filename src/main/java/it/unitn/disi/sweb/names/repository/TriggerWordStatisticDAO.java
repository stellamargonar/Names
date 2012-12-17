package it.unitn.disi.sweb.names.repository;

import java.util.List;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.TriggerWord;
import it.unitn.disi.sweb.names.model.TriggerWordStatistic;

public interface TriggerWordStatisticDAO {

	public void save(TriggerWordStatistic twetype);

	public void update(TriggerWordStatistic twetype);

	public void delete(TriggerWordStatistic twetype);

	public TriggerWordStatistic findById(int id);

	public TriggerWordStatistic findByTriggerWordEtype(TriggerWord triggerWord,
			EType eType);

	public List<TriggerWordStatistic> findByTriggerWord(TriggerWord triggerWord);

}
