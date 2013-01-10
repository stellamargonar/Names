package it.unitn.disi.sweb.names.repository;

import it.unitn.disi.sweb.names.model.Translation;

import java.util.List;

public interface DictionaryDAO {
	Translation create(Translation created);
	Translation getById(int id);
	List<String> findTranslations(String name);
	boolean contains(String source, String target);
}