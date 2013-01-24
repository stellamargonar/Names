package it.unitn.disi.sweb.names.utils.dataset;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "matchEntry")
public class MatchEntry {

	private Name name1;
	private Name name2;
	private boolean match;

	public MatchEntry() {
	}

	public MatchEntry(Name name1, Name name2, boolean match) {
		this.name1 = name1;
		this.name2 = name2;
		this.match = match;
	}

	@XmlElement(name = "correct")
	public boolean isMatch() {
		return match;
	}
	public void setMatch(boolean match) {
		this.match = match;
	}

	@XmlElement(name = "name1")
	public Name getName1() {
		return name1;
	}
	public void setName1(Name name1) {
		this.name1 = name1;
	}

	@XmlElement(name = "name2")
	public Name getName2() {
		return name2;
	}
	public void setName2(Name name2) {
		this.name2 = name2;
	}

}
