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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "usagestatistic", uniqueConstraints = @UniqueConstraint(columnNames = {
		"query", "fullname_id"}))
@SequenceGenerator(name = "stat_seq", sequenceName = "stat_id_seq")
@NamedQueries({
		@NamedQuery(name = "UsageStatistic.byQuery", query = "from UsageStatistic where lower(query) = :query"),
		@NamedQuery(name = "UsageStatistic.byQuerySelected", query = "from UsageStatistic where lower(query) = :query and selected=:selected")})
public class UsageStatistic {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stat_seq")
	private int id;

	@Column(name = "query")
	private String query;

	@ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.REMOVE})
	@JoinColumn(name = "fullname_id", nullable = false)
	private FullName selected;

	@Column(name = "frequency")
	private double frequency;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public FullName getSelected() {
		return selected;
	}

	public void setSelected(FullName selected) {
		this.selected = selected;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (query == null ? 0 : query.hashCode());
		result = prime * result
				+ (selected == null ? 0 : selected.hashCode());
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
		UsageStatistic other = (UsageStatistic) obj;
		if (query == null) {
			if (other.query != null) {
				return false;
			}
		} else if (!query.equals(other.query)) {
			return false;
		}
		if (selected == null) {
			if (other.selected != null) {
				return false;
			}
		} else if (!selected.equals(other.selected)) {
			return false;
		}
		return true;
	}

}
