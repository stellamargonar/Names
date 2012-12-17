package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Set;

import javax.persistence.*;

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
		@NamedQuery(name = "TriggerWord.variationsByTW", query = "from TriggerWord as trig where trig.variations = :tw") })
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

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "type_id", nullable = false)
	private TriggerWordType type;

	@OneToMany(mappedBy = "triggerWord", cascade = CascadeType.ALL)
	private Set<TriggerWordStatistic> eTypeStats;

	public TriggerWord() {
		super();
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

}
