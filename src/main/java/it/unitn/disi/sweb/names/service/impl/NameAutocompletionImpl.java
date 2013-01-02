package it.unitn.disi.sweb.names.service.impl;

import it.unitn.disi.sweb.names.model.EType;
import it.unitn.disi.sweb.names.model.FullName;
import it.unitn.disi.sweb.names.service.NameAutocompletion;
import it.unitn.disi.sweb.names.service.PrefixManager;
import it.unitn.disi.sweb.names.utils.Pair;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("nameAutocompletion")
public class NameAutocompletionImpl implements NameAutocompletion {

	private PrefixManager prefixManager;

	@Override
	public List<Pair<FullName, Double>> searchNamePrefix(String prefix) {
		return this.prefixManager.search(prefix);
	}

	@Override
	public List<Pair<FullName, Double>> searchNamePrefix(String prefix,
			EType etype) {
		return this.prefixManager.search(prefix, etype);
	}

	@Autowired
	public void setPrefixManager(PrefixManager prefixManager) {
		this.prefixManager = prefixManager;
	}

}
