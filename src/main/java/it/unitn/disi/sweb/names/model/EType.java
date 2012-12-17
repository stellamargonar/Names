package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Set;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: EType
 * 
 * Class representing an type per an entity. Examples of ETypes: Person,
 * Location, Organization, Event
 * 
 */
@Entity
@Table(name = "etype")
@SequenceGenerator(name = "etype_seq", sequenceName = "etype_id_seq")
@NamedQueries({
		@NamedQuery(name = "EType.findAll", query = "from EType "),
		@NamedQuery(name = "EType.findByName", query = "from EType where etype =:name")

})
public class EType implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etype_seq")
	private int id;

	@Column(name = "etype")
	private String etype;

	@OneToMany(mappedBy = "eType", cascade = CascadeType.ALL)
	private Set<NamedEntity> entities;

	@OneToMany(mappedBy = "eType", cascade = CascadeType.ALL)
	private Set<NameElement> nameElements;

	@OneToMany(mappedBy = "eType", cascade = CascadeType.ALL)
	private Set<TriggerWordStatistic> triggerWordStats;

	private static final long serialVersionUID = 1L;

	public EType() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEtype() {
		return this.etype;
	}

	public void setEtype(String etype) {
		this.etype = etype;
	}

	public Set<NamedEntity> getEntities() {
		return entities;
	}

	public void setEntities(Set<NamedEntity> entities) {
		this.entities = entities;
	}

	public Set<NameElement> getNameFields() {
		return nameElements;
	}

	public void setNameFields(Set<NameElement> nameFields) {
		this.nameElements = nameFields;
	}
}
