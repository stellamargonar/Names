package it.unitn.disi.sweb.names.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Entity implementation class for Entity: FullName
 *
 * Class representing one of the possible name for an entity. For full name is
 * intended the entire string representing the name of an entity.
 *
 * The relation between NamedEntity and FullName is 1 to N.
 *
 * Each FullName is composed by Tokens which are divided into IndividualNames
 * and TriggerWords.
 *
 */
@Entity
@Table(name = "fullname")
@SequenceGenerator(name = "fullname_seq", sequenceName = "fullname_id_seq")
@NamedQueries({
		@NamedQuery(name = "FullName.byName", query = "from FullName where lower(name) = :name"),
		@NamedQuery(name = "FullName.byNameNormalized", query = "from FullName where lower(nameNormalized) = :nameNormalized"),
		@NamedQuery(name = "FullName.byNameToCompare", query = "from FullName where lower(nameToCompare) = :nameToCompare"),
		@NamedQuery(name = "FullName.byNameEtype", query = "from FullName as fn where lower(name) = :name and fn.etype = :etype"),
		@NamedQuery(name = "FullName.byEntity", query = "from FullName as fn where fn.entity=:entity"),
		@NamedQuery(name = "FullName.byEntityName", query = "from FullName as fn where lower(name) = :name and fn.entity=:entity"),
		@NamedQuery(name = "FullName.variantForName", query = "from FullName as fn1 where fn1.entity.GUID in (select fullname.entity.GUID from FullName as fullname where lower(fullname.name) = :name and fullname.etype=:etype) and lower(fn1.name) != :name))"),
		@NamedQuery(name = "FullName.byToken", query = "from FullName where lower(name) like CONCAT('%', :name, '%')"),
		@NamedQuery(name = "FullName.byNgram", query = "from FullName where ABS(nGramCode - :code) < :diff"),
		@NamedQuery(name = "FullName.variantForNameNoEtype", query = "from FullName as fn1 where fn1.entity.GUID in (select fullname.entity.GUID from FullName as fullname where lower(fullname.name) = :name) and lower(fn1.name) != :name))"),})
public class FullName implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fullname_seq")
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "namenormalized")
	private String nameNormalized;

	@Column(name = "nametocompare")
	private String nameToCompare;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "entity_id", nullable = false)
	private NamedEntity entity;

	@Column(name = "ngramcode")
	private Integer nGramCode;

	@OneToMany(mappedBy = "fullName", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<NameToken> nameTokens;

	@OneToMany(mappedBy = "fullName", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<TriggerWordToken> triggerWordTokens;

	@Transient
	private Comparator<Object> tokenPositionComparator;

	@Column(name = "etype")
	private EType etype;

	private static final long serialVersionUID = 1L;

	public void setEtype(EType etype) {
		this.etype = etype;
	}

	public EType getEtype() {
		return etype;
	}

	public FullName() {
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

	/**
	 * accessor to the field representing the normalized name. Used for
	 * particular name search. At the moment for name normalized is intended the
	 * name where tokens are ordered in lexical graphic order
	 *
	 * @return the name normalized
	 */
	public String getNameNormalized() {
		return nameNormalized;
	}

	public void setNameNormalized(String nameNormalized) {
		this.nameNormalized = nameNormalized;
	}

	/**
	 * accessor to the field representing the string used for comparisong. This
	 * string contains the tokens which are relevant for comparison, that
	 * include:
	 * <ul>
	 * <li>all the name tokens</li>
	 * <li>trigger word tokens (depending on the value "to compare)</li>
	 * </ul>
	 *
	 * @return string used for comparison
	 */
	public String getNameToCompare() {
		return nameToCompare;
	}

	public void setNameToCompare(String nameToCompare) {
		this.nameToCompare = nameToCompare;
	}

	public Integer getNGramCode() {
		return nGramCode;
	}

	public void setNGramCode(Integer nGramCode) {
		this.nGramCode = nGramCode;
	}

	public NamedEntity getEntity() {
		return entity;
	}

	public void setEntity(NamedEntity entity) {
		this.entity = entity;
		etype = entity.getEType();
	}

	public int getGUID() {
		return entity.getGUID();
	}

	public Integer getnGramCode() {
		return nGramCode;
	}

	public void setnGramCode(Integer nGramCode) {
		this.nGramCode = nGramCode;
	}

	public Set<NameToken> getNameTokens() {
		return nameTokens;
	}

	public void setNameTokens(Set<NameToken> nameTokens) {
		this.nameTokens = nameTokens;
	}

	public Set<TriggerWordToken> getTriggerWordTokens() {
		return triggerWordTokens;
	}

	public void setTriggerWordTokens(Set<TriggerWordToken> triggerWordTokens) {
		this.triggerWordTokens = triggerWordTokens;
	}

	public List<NameToken> getNameTokensOrdered() {
		ArrayList<NameToken> list = new ArrayList<NameToken>(getNameTokens());
		Collections.sort(list, tokenPositionComparator);
		return list;
	}

	public List<TriggerWordToken> getTriggerWordTokensOrdered() {
		ArrayList<TriggerWordToken> list = new ArrayList<TriggerWordToken>(
				getTriggerWordTokens());
		Collections.sort(list, tokenPositionComparator);
		return list;
	}

	public void addNameToken(NameToken nameToken) {
		if (getNameTokens() == null) {
			nameTokens = new HashSet<>();
		}
		nameTokens.add(nameToken);
	}

	public void addTriggerWordToken(TriggerWordToken token) {
		if (getTriggerWordTokens() == null) {
			triggerWordTokens = new HashSet<>();
		}
		triggerWordTokens.add(token);
	}

	@Autowired
	public void setTokenPositionComparator(
			Comparator<Object> tokenPositionComparator) {
		this.tokenPositionComparator = tokenPositionComparator;
	}
	public Comparator<Object> getTokenPositionComparator() {
		return tokenPositionComparator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (entity == null ? 0 : entity.hashCode());
		result = prime * result + id;
		result = prime * result + (name == null ? 0 : name.hashCode());
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
		FullName other = (FullName) obj;
		if (entity == null) {
			if (other.entity != null) {
				return false;
			}
		} else if (!entity.equals(other.entity)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name;
		// return "FullName [id=" + id + ", name=" + name + ", entity=" + entity
		// + ", nameTokens=" + nameTokens + ", triggerWordTokens="
		// + triggerWordTokens + "]";
	}

}
