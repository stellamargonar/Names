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

}
