package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity implementation class for Entity: IndividualName
 *
 * Class representing an individual name, which can compose a full name.
 * Examples of individual names: Mario, Garda, Fausto, Trento
 *
 * Some names can have translations which are stored in a specific table
 *
 */
@Entity
@Table(name = "individualname", uniqueConstraints = @UniqueConstraint(columnNames = {
		"name", "element_id"}))
@SequenceGenerator(name = "individualname_seq", sequenceName = "individualname_id_seq")
@NamedQueries({
		@NamedQuery(name = "IndividualName.byName", query = "from IndividualName where lower(name) = :name"),
		@NamedQuery(name = "IndividualName.byNameEtype", query = "from IndividualName where lower(name) = :name and nameElement.eType=:etype"),
		@NamedQuery(name = "IndividualName.translation", query = "select i from IndividualName i join i.translations o where (lower(o.name) = :name1 and lower(i.name) = :name2) or (lower(o.name) = :name2 and lower(i.name) = :name1)"),
		@NamedQuery(name = "IndividualName.alltranslation1", query = "select o from IndividualName i join i.translations o where lower(i.name)=:name"),
		@NamedQuery(name = "IndividualName.alltranslation2", query = "select i from IndividualName i join i.translations o where lower(o.name)=:name"),
		@NamedQuery(name = "IndividualName.byNgram", query = "from IndividualName where ABS(nGramCode - :ngram) < :diff"),
		@NamedQuery(name = "IndividualName.byNameElement", query = "from IndividualName where (name = :name or lower(name)=:name) and nameElement=:element")})
public class IndividualName implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "individualname_seq")
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "frequency")
	private int frequency;

	@ManyToMany
	@JoinTable(name = "nametranslation", joinColumns = @JoinColumn(name = "source_name_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "target_name_id", referencedColumnName = "id"))
	private Set<IndividualName> translations;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "element_id", nullable = false)
	private NameElement nameElement;

	@Column(name = "ngram")
	private int nGramCode;

	private static final long serialVersionUID = 1L;

	public IndividualName() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public Set<IndividualName> getTranslations() {
		return translations;
	}

	public void setTranslations(Set<IndividualName> translations) {
		this.translations = translations;
	}

	public NameElement getNameElement() {
		return nameElement;
	}

	public void setNameElement(NameElement nameElement) {
		this.nameElement = nameElement;
	}

	public void addTranslation(IndividualName t) {
		if (getTranslations() == null) {
			translations = new HashSet<>();
		}
		getTranslations().add(t);
	}
	public void addTranslations(Collection<IndividualName> t) {
		if (getTranslations() == null) {
			translations = new HashSet<>();
		}
		getTranslations().addAll(t);
	}

	public int getnGramCode() {
		return nGramCode;
	}
	public void setnGramCode(int nGramCode) {
		this.nGramCode = nGramCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result
				+ (nameElement == null ? 0 : nameElement.hashCode());
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
		IndividualName other = (IndividualName) obj;

		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (nameElement == null) {
			if (other.nameElement != null) {
				return false;
			}
		} else if (!nameElement.equals(other.nameElement)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "IndividualName [name=" + name + "]";
	}

}
