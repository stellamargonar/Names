package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import java.lang.String;
import java.util.Set;

import javax.persistence.*;

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
		@NamedQuery(name = "NamedEntity.byName", query = "from NamedEntity where GUID in (select fullname.entity from FullName as fullname where name= :name)"),
		@NamedQuery(name = "NamedEntity.byNameEtype", query = "from NamedEntity where GUID in (select fullname.entity from FullName as fullname where name= :name) and etype=:etype") })
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

	@OneToMany(mappedBy = "entity", cascade = CascadeType.ALL)
	private Set<FullName> names;

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

	public void setNames(Set<FullName> names) {
		this.names = names;
	}

	public Set<FullName> getNames() {
		return names;
	}
}
