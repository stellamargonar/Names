package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.Translation;

public interface DictionaryDAO {
    public Translation create(Translation created);
    public Translation getById(int id);
}
