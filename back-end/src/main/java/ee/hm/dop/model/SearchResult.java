package ee.hm.dop.model;

import java.util.Collections;
import java.util.List;

public class SearchResult {

    private List<Searchable> items;

    private long totalResults;

    private long start;

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
