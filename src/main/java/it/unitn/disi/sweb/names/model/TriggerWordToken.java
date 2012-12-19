package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: TriggerWordToken
 * 
 * Class representing the relation (N to N) between FullName and TriggerWord.
 * 
 * In particular it provides the information the position it appears in the
 * fullname
 */
@Entity
@Table(name = "triggerwordtoken", uniqueConstraints = @UniqueConstraint(columnNames = {
		"full_name_id", "trigger_word_id" }))
@SequenceGenerator(name = "twtoken_seq", sequenceName = "twtoken_id_seq")
@NamedQueries({
		@NamedQuery(name = "TWToken.byFullName", query = "from TriggerWordToken where fullName=:name"),
		@NamedQuery(name = "TWToken.byTriggerWord", query = "from TriggerWordToken where triggerWord=:tw"),
		@NamedQuery(name = "TWToken.byTriggerWordFullName", query = "from TriggerWordToken where triggerWord=:tw and fullName= :fullname") })
public class TriggerWordToken implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "twtoken_seq")
	private int id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "full_name_id", nullable = false)
	private FullName fullName;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "trigger_word_id", nullable = false)
	private TriggerWord triggerWord;

	@Column(name = "position")
	private int position;

	private static final long serialVersionUID = 1L;

	public TriggerWordToken() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FullName getFullName() {
		return fullName;
	}

	public void setFullName(FullName fullName) {
		this.fullName = fullName;
	}

	public TriggerWord getTriggerWord() {
		return triggerWord;
	}

	public void setTriggerWord(TriggerWord triggerWord) {
		this.triggerWord = triggerWord;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((triggerWord == null) ? 0 : triggerWord.hashCode());
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
		TriggerWordToken other = (TriggerWordToken) obj;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (id != other.id)
			return false;
		if (triggerWord == null) {
			if (other.triggerWord != null)
				return false;
		} else if (!triggerWord.equals(other.triggerWord))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TriggerWordToken [triggerWord=" + triggerWord + ", position="
				+ position + "]";
	}

	
}
