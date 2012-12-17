package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

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
@Table(name = "nameelement", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"elementname", "etype_id" }) })
@SequenceGenerator(name = "nameelement_seq", sequenceName = "nameelement_id_seq")
@NamedQueries({
		@NamedQuery(name = "NameElement.byName", query = "from NameElement where elementName= :name"),
		@NamedQuery(name = "NameElement.all", query = "from NameElement"),
		@NamedQuery(name = "NameElement.byEtype", query = "from NameElement where eType = :eType") })
public class NameElement implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nameelement_seq")
	private int id;

	@Column(name = "elementname")
	private String elementName;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "etype_id", nullable = false)
	private EType eType;

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
		return eType;
	}

	public void setEtype(EType etype) {
		this.eType = etype;
	}
}
