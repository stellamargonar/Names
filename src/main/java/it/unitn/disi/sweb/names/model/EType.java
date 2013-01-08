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
 * Entity implementation class for Entity: EType
 *
 * Class representing an type per an entity. Examples of ETypes: Person,
 * Location, Organization, Event
 *
 */
@Entity
@Table(name = "etype", uniqueConstraints=@UniqueConstraint(columnNames="etype"))
@SequenceGenerator(name = "etype_seq", sequenceName = "etype_id_seq")
@NamedQueries({
		@NamedQuery(name = "EType.findAll", query = "from EType "),
		@NamedQuery(name = "EType.findByName", query = "from EType where lower(etype) =:name")

})
public class EType implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etype_seq")
	private int id;

	@Column(name = "etype")
	private String etype;

	private static final long serialVersionUID = 1L;

	public EType() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEtype() {
		return etype;
	}

	public void setEtype(String etype) {
		this.etype = etype;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (etype == null ? 0 : etype.hashCode());
		result = prime * result + id;
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
		EType other = (EType) obj;
		if (etype == null) {
			if (other.etype != null) {
				return false;
			}
		} else if (!etype.equals(other.etype)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		return true;
	}

}
