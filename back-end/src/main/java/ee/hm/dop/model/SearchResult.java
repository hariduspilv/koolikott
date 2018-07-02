package ee.hm.dop.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SearchResult {

    private List<Searchable> items;
    private Map<String, SearchResult> groups;
    private long totalResults;
    private long start;
    private long distinctIdCount;

    public SearchResult() {
        items = Collections.emptyList();
        start = 0L;
        totalResults = 0L;
    }

    public SearchResult(List<Searchable> items, long totalResults, long start) {
        this.items = items;
        this.totalResults = totalResults;
        this.start = start;
    }

    public SearchResult(Map<String, SearchResult> groups) {
        this.groups = groups;
    }

    public SearchResult(Map<String, SearchResult> groups, long totalResults) {
        this.groups = groups;
        this.totalResults = totalResults;
    }

    public long getDistinctIdCount() {
        return distinctIdCount;
    }

    public void setDistinctIdCount(long distinctIdCount) {
        this.distinctIdCount = distinctIdCount;
    }

    public Map<String, SearchResult> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, SearchResult> groups) {
        this.groups = groups;
    }

    public List<Searchable> getItems() {
        return items;
    }

    public void setItems(List<Searchable> items) {
        this.items = items;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }
}
