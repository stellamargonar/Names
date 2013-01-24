package it.unitn.disi.sweb.names.utils.dataset;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MapAdapter
		extends
			XmlAdapter<PairList, List<Entry<String, String>>> {
	@Override
	public PairList marshal(List<Entry<String, String>> arg0) throws Exception {
		PairList mapElements = new PairList();
		for (Entry<String, String> entry : arg0) {
			mapElements.add(new Pair(entry.getKey(), entry.getValue()));
		}
		return mapElements;
	}

	@Override
	public List<Entry<String, String>> unmarshal(PairList arg0)
			throws Exception {
		List<Entry<String, String>> r = new ArrayList<>();
		for (Pair mapelement : arg0.getList()) {
			r.add(new AbstractMap.SimpleEntry(mapelement.key, mapelement.value));
		}
		return r;
	}

}

class PairList {
	private ArrayList<Pair> list;

	public void add(Pair p) {
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add(p);
	}

	@XmlElement(name = "token")
	public ArrayList<Pair> getList() {
		return list;
	}
	public void setList(ArrayList<Pair> list) {
		this.list = list;
	}
}

class Pair {

	@XmlAttribute(name = "string")
	String key;
	@XmlAttribute(name = "element")
	String value;

	Pair() {

	}

	Pair(String key, String value) {
		this.key = key;
		this.value = value;
	}
}