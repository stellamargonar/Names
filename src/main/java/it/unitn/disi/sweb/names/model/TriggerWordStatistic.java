package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import javax.persistence.*;

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
		"trigger_word_id", "etype_id" }))
@SequenceGenerator(name = "twstatistic_seq", sequenceName = "twstatistic_id_seq")
@NamedQueries({
		@NamedQuery(name = "TWStatistic.byTriggerWord", query = "from TriggerWordStatistic as twe where triggerWord = :tw order by twe.frequency"),
		@NamedQuery(name = "TWStatistic.byTriggerWordEtype", query = "from TriggerWordStatistic as twe where triggerWord = :tw and eType=:etype") })
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

	private long frequency;

	private static final long serialVersionUID = 1L;

	public TriggerWordStatistic() {
		super();
	}

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}
}
