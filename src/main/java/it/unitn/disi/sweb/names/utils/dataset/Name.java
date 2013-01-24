package it.unitn.disi.sweb.names.utils.dataset;

import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "nameEntry")
public class Name {

	private String name;

	/**
	 * list of tokens, represented by the string corresponding, and the name of
	 * the element for that token (name element or triggerwordtype) tokens are
	 * orderd depending on where they appear in the name
	 */
	private List<Entry<String, String>> tokens;

	public Name() {
	}
	public Name(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "name", required = true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "tokens")
	@XmlJavaTypeAdapter(MapAdapter.class)
	public List<Entry<String, String>> getTokens() {
		return tokens;
	}
	public void setTokens(List<Entry<String, String>> tokens) {
		this.tokens = tokens;
	}

}
