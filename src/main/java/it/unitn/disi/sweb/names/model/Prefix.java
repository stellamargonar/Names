/**
 *
 */
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

/**
 * @author stella
 *
 */
@Entity
@Table(name = "prefix")
@SequenceGenerator(name = "prefix_seq", sequenceName = "prefix_id_seq")
@NamedQueries({
		@NamedQuery(name = "Prefix.byPrefix", query = "from Prefix where prefix=:prefix"),
		@NamedQuery(name = "Prefix.byPrefixSelected", query = "from Prefix where prefix=:prefix and selected=:name")})
public class Prefix {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prefix_seq")
	private int id;

	@Column(name = "prefix")
	private String prefix;

	@ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
	@JoinColumn(name = "fullname_id", nullable = false)
	private FullName selected;

	@Column(name = "frequency")
	private double frequency;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public FullName getSelected() {
		return this.selected;
	}

	public void setSelected(FullName selected) {
		this.selected = selected;
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
				+ (this.prefix == null ? 0 : this.prefix.hashCode());
		result = prime * result
				+ (this.selected == null ? 0 : this.selected.hashCode());
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
		Prefix other = (Prefix) obj;
		if (this.prefix == null) {
			if (other.prefix != null) {
				return false;
			}
		} else if (!this.prefix.equals(other.prefix)) {
			return false;
		}
		if (this.selected == null) {
			if (other.selected != null) {
				return false;
			}
		} else if (!this.selected.equals(other.selected)) {
			return false;
		}
		return true;
	}

}
