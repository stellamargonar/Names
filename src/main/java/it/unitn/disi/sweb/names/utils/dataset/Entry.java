package it.unitn.disi.sweb.names.utils.dataset;

import it.unitn.disi.sweb.names.model.EType;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Entry {

	private List<Name> names;
	private List<Name> variations;
	private EType etype;

	public Entry() {

	}

	public Entry(EType etype) {
		this.etype = etype;
	}

	void addName(Name name) {
		if (names == null) {
			names = new ArrayList<>();
		}
		if (name != null) {
			names.add(name);
		}
	}

	void addVariation(Name variation) {
		if (variations == null) {
			variations = new ArrayList<>();
		}
		if (variation != null) {
			variations.add(variation);
		}
	}

	@XmlElement(name = "variant")
	public List<Name> getNames() {
		return names;
	}
	public void setNames(List<Name> names) {
		this.names = names;
	}

	@XmlElement(name = "variation")
	public List<Name> getVariations() {
		return variations;
	}
	public void setVariations(List<Name> variations) {
		this.variations = variations;
	}

	@XmlElement(name = "etype")
	public EType getEtype() {
		return etype;
	}
	public void setEtype(EType etype) {
		this.etype = etype;
	}


	@Override
	public String toString() {
		String r = "";
		for (Name f : names) {
			r += f.getName() + " ";
		}
		for (Name v : variations) {
			r += v.getName() + " ";
		}
		return r + "\n";
	}

}
