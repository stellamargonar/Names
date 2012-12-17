package it.unitn.disi.sweb.names.service;

import it.unitn.disi.sweb.names.model.EType;

public interface NameMatch {
	
	public double stringSimilarity(String name1, String name2, EType eType);

	public double dictionaryLookup(String name1, String name2, EType eType);
	

}
