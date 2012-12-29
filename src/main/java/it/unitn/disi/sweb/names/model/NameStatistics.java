package it.unitn.disi.sweb.names.model;

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

@Entity
@Table(name = "namestatistic")
@SequenceGenerator(name = "namestat_seq", sequenceName = "namestat_id_seq")
@NamedQueries({
		@NamedQuery(name = "NameStatistic.byName", query = "from NameStatistics where name= :name"),
		@NamedQuery(name = "NameStatistic.byNameElement", query = "from NameStatistics where name= :name and nameElement=:element")})
public class NameStatistics {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "namestat_seq")
	int id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "individualname_id", nullable = false)
	IndividualName name;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "nameelement_id", nullable = false)
	NameElement nameElement;

	@Column(name = "frequency")
	double frequency;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public IndividualName getName() {
		return this.name;
	}

	public void setName(IndividualName name) {
		this.name = name;
	}

	public NameElement getNameElement() {
		return this.nameElement;
	}

	public void setNameElement(NameElement nameElement) {
		this.nameElement = nameElement;
	}

	public double getFrequency() {
		return this.frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (this.name == null ? 0 : this.name.hashCode());
		result = prime * result
				+ (this.nameElement == null ? 0 : this.nameElement.hashCode());
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
		NameStatistics other = (NameStatistics) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.nameElement == null) {
			if (other.nameElement != null) {
				return false;
			}
		} else if (!this.nameElement.equals(other.nameElement)) {
			return false;
		}
		return true;
	}

}
