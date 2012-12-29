package it.unitn.disi.sweb.names.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity implementation class for Entity: TriggerWordEType
 *
 * Represents the relation between the trigger words and etypes. Represents the
 * frequency that a triggerword appears in a name of a particular etype.
 *
 * Used for guessing the etype of a name when this information is not available
 */
@Entity
@Table(name = "triggerwordstatistic", uniqueConstraints = @UniqueConstraint(columnNames = {
		"trigger_word_id", "etype_id"}))
@SequenceGenerator(name = "twstatistic_seq", sequenceName = "twstatistic_id_seq")
@NamedQueries({
		@NamedQuery(name = "TWStatistic.byTriggerWord", query = "from TriggerWordStatistic as twe where triggerWord = :tw order by twe.frequency"),
		@NamedQuery(name = "TWStatistic.byTriggerWordEtype", query = "from TriggerWordStatistic as twe where triggerWord = :tw and eType=:etype")})
public class TriggerWordStatistic implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "twstatistic_seq")
	private int id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "trigger_word_id", nullable = false)
	private TriggerWord triggerWord;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "etype_id", nullable = false)
	private EType eType;

	@Column(name = "frequency")
	private long frequency;

	private static final long serialVersionUID = 1L;

	public TriggerWordStatistic() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getFrequency() {
		return this.frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	public TriggerWord getTriggerWord() {
		return this.triggerWord;
	}

	public void setTriggerWord(TriggerWord triggerWord) {
		this.triggerWord = triggerWord;
	}

	public EType geteType() {
		return this.eType;
	}

	public void seteType(EType eType) {
		this.eType = eType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (this.eType == null ? 0 : this.eType.hashCode());
		result = prime * result + this.id;
		result = prime * result
				+ (this.triggerWord == null ? 0 : this.triggerWord.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TriggerWordStatistic other = (TriggerWordStatistic) obj;
		if (this.eType == null) {
			if (other.eType != null) {
				return false;
			}
		} else if (!this.eType.equals(other.eType)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (this.triggerWord == null) {
			if (other.triggerWord != null) {
				return false;
			}
		} else if (!this.triggerWord.equals(other.triggerWord)) {
			return false;
		}
		return true;
	}

}
