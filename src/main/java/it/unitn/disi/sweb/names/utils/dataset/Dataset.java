package it.unitn.disi.sweb.names.utils.dataset;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "it.unitn.disi.sweb.names", name = "dataset")
public class Dataset {
	private List<Entry> entries;
	private List<MatchEntry> matchEntries;
	private List<Result> results;
	private List<SearchResult> searchResult;

	void addEntry(Entry e) {
		if (getEntries() == null) {
			entries = new ArrayList<>();
		}
		if (e != null) {
			entries.add(e);
		}
	}

	void addMatchEntry(MatchEntry e) {
		if (matchEntries == null) {
			matchEntries = new ArrayList<>();
		}
		if (e != null) {
			matchEntries.add(e);
		}
	}

	void addAllMatch(List<MatchEntry> list) {
		if (matchEntries == null) {
			matchEntries = new ArrayList<>();
		}
		if (list != null) {
			matchEntries.addAll(list);
		}
	}
	void addResult(Result r) {
		if (results == null) {
			results = new ArrayList<>();
		}
		results.add(r);
	}
	void addResult(SearchResult r) {
		if (searchResult == null) {
			searchResult = new ArrayList<>();
		}
		searchResult.add(r);
	}

	@XmlElement(name = "entry")
	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	@XmlElementWrapper(name = "match")
	public List<MatchEntry> getMatchEntries() {
		return matchEntries;
	}

	public void setMatchEntries(List<MatchEntry> matchEntries) {
		this.matchEntries = matchEntries;
	}

	@XmlElementWrapper(name = "results")
	@XmlElement(name = "result")
	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}

	@XmlElementWrapper(name = "searchResults")
	@XmlElement(name = "result")
	public List<SearchResult> getSearchResult() {
		return searchResult;
	}
	public void setSearchResult(List<SearchResult> searchResult) {
		this.searchResult = searchResult;
	}
}
