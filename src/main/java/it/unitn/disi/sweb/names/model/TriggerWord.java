package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: TriggerWord
 * 
 * Class representing trigger words, which are words part of a full name, but
 * does not represent a name. Example : Dottor, Professor, Lake
 * 
 * Some trigger can have translations and variations which are stored in a
 * specific table
 * 
 */
@Entity
@Table(name = "triggerword")
@SequenceGenerator(name = "triggerword_seq", sequenceName = "triggerword_id_seq")
@NamedQueries({
		@NamedQuery(name = "TriggerWord.byTW", query = "from TriggerWord where triggerWord = :tw"),
		@NamedQuery(name = "TriggerWord.variationsByTW", query = "from TriggerWord as trig where trig.variations = :tw"),
		@NamedQuery(name = "TriggerWord.byTWEtype", query = "from TriggerWord as trig where triggerWord = :tw and trig.type.eType=:etype"),
		@NamedQuery(name = "TriggerWord.isVariations", query = "select t from TriggerWord as t join t.variations o where (o.triggerWord = :t1 and t.triggerWord=:t2) or (o.triggerWord=:t2 and t.triggerWord=:t1)") })
public class TriggerWord implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "triggerword_seq")
	private int id;

	@Column(name = "triggerword")
	private String triggerWord;
	private static final long serialVersionUID = 1L;

	@ManyToMany
	@JoinTable(name = "triggerwordvariations", joinColumns = @JoinColumn(name = "source_tw_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "target_tw_id", referencedColumnName = "id"))
	private Set<TriggerWord> variations;

	@OneToMany(mappedBy = "triggerWord", cascade = CascadeType.ALL)
	private Set<TriggerWordToken> tokens;

	@ManyToOne
	@JoinColumn(name = "type_id", nullable = false)
	private TriggerWordType type;

	@OneToMany(mappedBy = "triggerWord", cascade = CascadeType.ALL)
	private Set<TriggerWordStatistic> eTypeStats;

	public TriggerWord() {
		super();
	}

	public TriggerWord(String triggerWord, TriggerWordType type) {
		this.triggerWord = triggerWord;
		this.type = type;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTriggerWord() {
		return this.triggerWord;
	}

	public void setTriggerWord(String triggerWord) {
		this.triggerWord = triggerWord;
	}

	public Set<TriggerWord> getVariations() {
		return variations;
	}

	public void setVariations(Set<TriggerWord> variations) {
		this.variations = variations;
	}

	public TriggerWordType getType() {
		return type;
	}

	public void setType(TriggerWordType type) {
		this.type = type;
	}

	public void addVariation(TriggerWord variation) {
		if (getVariations() == null)
			variations = new HashSet();
		variations.add(variation);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((triggerWord == null) ? 0 : triggerWord.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TriggerWord other = (TriggerWord) obj;
		if (id != other.id)
			return false;
		if (triggerWord == null) {
			if (other.triggerWord != null)
				return false;
		} else if (!triggerWord.equals(other.triggerWord))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TriggerWord [triggerWord=" + triggerWord + "]";
	}

}
