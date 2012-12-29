package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: TriggerWordType Correspoing of
 * NameField for IndividualName.
 *
 * Represents the possible types of trigger words the we can have in a FullName.
 * Depending on their type a trigger word could be included or not in the
 * comparsion.
 *
 * For example, fullName = "Professor Fausto Giunchiglia". In this case the
 * trigger word = "Professor", and should not be used for comparison, since the
 * name would not match the string "Fausto Giunchiglia" representing the same
 * person.
 *
 * Other example, fullName = "Garda Lake". Triggerword = "Lake". In this case it
 * must be considered, since otherwise this name would match with for example
 * "Garda City" which does not represent the same entity
 */
@Entity
@Table(name = "triggerwordtype")
@SequenceGenerator(name = "twtype_seq", sequenceName = "twtype_id_seq")
@NamedQueries({
		@NamedQuery(name = "TWType.findAll", query = "from TriggerWordType"),
		@NamedQuery(name = "TWType.findByName", query = "from TriggerWordType where type=:name"),
		@NamedQuery(name = "TWType.findByEtype", query = "from TriggerWordType where eType=:etype")})
public class TriggerWordType implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "twtype_seq")
	private int id;

	@Column(name = "type")
	private String type;

	@Column(name = "comparable")
	private boolean comparable;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "etype_id", nullable = false)
	private EType eType;

	@OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
	private Set<TriggerWord> triggerWords;
	private static final long serialVersionUID = 1L;

	public TriggerWordType() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getComparable() {
		return this.comparable;
	}

	public void setComparable(boolean comparable) {
		this.comparable = comparable;
	}

	public void seteType(EType eType) {
		this.eType = eType;
	}

	public EType geteType() {
		return this.eType;
	}

	public boolean isComparable() {
		return getComparable();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (this.eType == null ? 0 : this.eType.hashCode());
		result = prime * result + this.id;
		result = prime * result
				+ (this.type == null ? 0 : this.type.hashCode());
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
		TriggerWordType other = (TriggerWordType) obj;
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
		if (this.type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!this.type.equals(other.type)) {
			return false;
		}
		return true;
	}

}
