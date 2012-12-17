package it.unitn.disi.sweb.names.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity implementation class for Entity: Translation
 * 
 * Class representing the dictionary where are contained all the tranlsation in
 * the system. Those translations are retrieved from external sources (see
 * Heiner tranlsationDictionary)
 * 
 */

@Table(name = "translation", uniqueConstraints = @UniqueConstraint(columnNames = {
		"namesource", "nametarget" }))
@SequenceGenerator(name = "trans_seq", sequenceName = "trans_id_seq")
@Entity
@NamedQueries({ @NamedQuery(name = "Translation.byName", query = "from Translation where nameSource = :name or nameTarget=:name") })
public class Translation implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_seq")
	private int id;

	@Column(name = "namesource")
	private String nameSource;
	@Column(name = "nametarget")
	private String nameTarget;
	private static final long serialVersionUID = 1L;

	public Translation() {
		super();
	}

	public Translation(String source, String target) {
		this.nameSource = source;
		this.nameTarget = target;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNameSource() {
		return this.nameSource;
	}

	public void setNameSource(String nameSource) {
		this.nameSource = nameSource;
	}

	public String getNameTarget() {
		return this.nameTarget;
	}

	public void setNameTarget(String nameTarget) {
		this.nameTarget = nameTarget;
	}

}
