package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import javax.persistence.UniqueConstraint;

/**
 * Entity implementation class for Entity: NameElement
 *
 * Class representing the fields that an individual name can represent (example:
 * firstname, surname). Each name field is identified by an id.
 *
 * Each nameField is specific for a particular Etype.
 *
 */
@Entity
@Table(name = "nameelement", uniqueConstraints = {@UniqueConstraint(columnNames = {
		"elementname", "etype_id"})})
@SequenceGenerator(name = "nameelement_seq", sequenceName = "nameelement_id_seq")
@NamedQueries({
		@NamedQuery(name = "NameElement.byName", query = "from NameElement where elementName= :name"),
		@NamedQuery(name = "NameElement.all", query = "from NameElement"),
		@NamedQuery(name = "NameElement.byEtype", query = "from NameElement where eType = :eType"),
		@NamedQuery(name = "NameElement.byNameEtype", query = "from NameElement where eType = :eType and elementName=:name")})
public class NameElement implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nameelement_seq")
	private int id;

	@Column(name = "elementname")
	private String elementName;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinColumn(name = "etype_id", nullable = false)
	private EType eType;

	@OneToMany(mappedBy = "nameElement", cascade = CascadeType.ALL)
	private Set<IndividualName> individualNames;

	private static final long serialVersionUID = 1L;

	public NameElement() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getElementName() {
		return this.elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public EType getEtype() {
		return this.eType;
	}

	public void setEtype(EType etype) {
		this.eType = etype;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		 result = prime * result + (this.eType == null ? 0 : this.eType.hashCode());
		result = prime * result
				+ (this.elementName == null ? 0 : this.elementName.hashCode());
		result = prime * result + this.id;
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
		NameElement other = (NameElement) obj;
		if (this.eType == null) {
			if (other.eType != null) {
				return false;
			}
		} else if (!this.eType.equals(other.eType)) {
			return false;
		}
		if (this.elementName == null) {
			if (other.elementName != null) {
				return false;
			}
		} else if (!this.elementName.equals(other.elementName)) {
			return false;
		}
		if (this.id != other.id) {
			return false;
		}
		return true;
	}
}
