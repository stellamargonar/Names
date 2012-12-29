package it.unitn.disi.sweb.names.utils;

import it.unitn.disi.sweb.names.model.NameToken;
import it.unitn.disi.sweb.names.model.TriggerWordToken;

import java.util.Comparator;

public class TokenPositionComparator implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 instanceof NameToken && o2 instanceof NameToken) {
			return ((NameToken)o1).getPosition() - ((NameToken)o2).getPosition();
		} else if (o1 instanceof TriggerWordToken && o2 instanceof TriggerWordToken) {
			return ((TriggerWordToken)o1).getPosition() - ((TriggerWordToken)o2).getPosition();
		} else {
			throw new ClassCastException();
		}
	}
}
