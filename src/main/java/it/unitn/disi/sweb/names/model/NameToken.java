package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import javax.persistence.*;

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

	public IndividualName getIndividualName() {
		return individualName;
	}

	public void setIndividualName(IndividualName individualName) {
		this.individualName = individualName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "NameToken [individualName=" + individualName + ", position="
				+ position + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((individualName == null) ? 0 : individualName.hashCode());
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
		NameToken other = (NameToken) obj;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (id != other.id)
			return false;
		if (individualName == null) {
			if (other.individualName != null)
				return false;
		} else if (!individualName.equals(other.individualName))
			return false;
		return true;
	}
}
