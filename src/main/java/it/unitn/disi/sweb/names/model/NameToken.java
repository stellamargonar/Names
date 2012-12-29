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
 * Entity implementation class for Entity: NameToken
 *
 * Class representing the relation (N to N) between FullName and IndividualName.
 *
 * In particular it provides the information about which element each name
 * represents and in which position it appears in the fullname
 */
@Entity
@Table(name = "nametoken", uniqueConstraints = @UniqueConstraint(columnNames = {
		"full_name_id", "name_id" }))
@SequenceGenerator(name = "nametoken_seq", sequenceName = "nametoken_id_seq")
@NamedQueries({
		@NamedQuery(name = "NameToken.byFullName", query = "from NameToken where fullName= :fullName"),
		@NamedQuery(name = "NameToken.byIndividualName", query = "from NameToken where individualName= :individualName"),
		@NamedQuery(name = "NameToken.byFullIndividualName", query = "from NameToken where individualName= :individualName and fullName=:fullname)") })
public class NameToken implements Serializable {



	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nametoken_seq")
	private int id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "full_name_id", nullable = false)
	private FullName fullName;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "name_id", nullable = false)
	private IndividualName individualName;

	@Column(name = "position")
	private int position;

	private static final long serialVersionUID = 1L;

	public NameToken() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public FullName getFullName() {
		return this.fullName;
	}

	public void setFullName(FullName fullName) {
		this.fullName = fullName;
	}

	public IndividualName getIndividualName() {
		return this.individualName;
	}

	public void setIndividualName(IndividualName individualName) {
		this.individualName = individualName;
	}

	public int getPosition() {
		return this.position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "NameToken [individualName=" + this.individualName + ", position="
				+ this.position + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (this.fullName == null ? 0 : this.fullName.hashCode());
		result = prime * result + this.id;
		result = prime * result
				+ (this.individualName == null ? 0 : this.individualName.hashCode());
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
		NameToken other = (NameToken) obj;
		if (this.fullName == null) {
			if (other.fullName != null) {
				return false;
			}
		} else if (!this.fullName.equals(other.fullName)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		if (this.individualName == null) {
			if (other.individualName != null) {
				return false;
			}
		} else if (!this.individualName.equals(other.individualName)) {
			return false;
		}
		return true;
	}
}
