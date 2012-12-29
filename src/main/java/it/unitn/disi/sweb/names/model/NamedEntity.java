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

/**
 * Entity implementation class for Entity: Entity
 *
 * Class representing an Entity. Each entity is identified by a unique GUID
 * (Global Unique Identifier), and belongs to an EType. It can have multiple
 * names (see FullName)
 *
 */

@Entity
@Table(name = "namedentity")
@SequenceGenerator(name = "entity_seq", sequenceName = "entity_id_seq")
@NamedQueries({
		@NamedQuery(name = "NamedEntity.byName", query = "select fullname.entity from FullName as fullname where name= :name"),
		@NamedQuery(name = "NamedEntity.byNameEtype", query = "from NamedEntity where GUID in (select fullname.entity from FullName as fullname where name= :name) and eType=:etype"),
		@NamedQuery(name = "NamedEntity.byNameUrl", query = "from NamedEntity where GUID in (select fullname.entity from FullName as fullname where name= :name) and url=:url"),
		@NamedQuery(name = "NamedEntity.byUrl", query = "from NamedEntity where url=:url"),
		@NamedQuery(name = "NamedEntity.byEType", query = "from NamedEntity where eType=:etype") })
public class NamedEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entity_seq")
	private int GUID;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "etype_id", nullable = false)
	private EType eType;

	@Column(name = "url")
	private String url;

	private static final long serialVersionUID = 1L;

	public NamedEntity() {
		super();
	}

	public int getGUID() {
		return this.GUID;
	}

	public void setGUID(int GUID) {
		this.GUID = GUID;
	}

	public EType getEType() {
		return this.eType;
	}

	public void setEType(EType eType) {
		this.eType = eType;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.GUID;
		result = prime * result + (this.eType == null ? 0 : this.eType.hashCode());
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
		NamedEntity other = (NamedEntity) obj;
		if (this.GUID != other.GUID) {
			return false;
		}
		if (this.eType == null) {
			if (other.eType != null) {
				return false;
			}
		} else if (!this.eType.equals(other.eType)) {
			return false;
		}
		return true;
	}

}
