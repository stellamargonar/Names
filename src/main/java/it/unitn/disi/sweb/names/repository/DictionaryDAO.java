package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.Translation;

public interface DictionaryDAO {
	Translation create(Translation created);
	Translation getById(int id);
}