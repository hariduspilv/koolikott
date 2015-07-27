package ee.hm.dop.model.solr;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {

    @JsonProperty("docs")
    private List<Document> documents;

    @JsonProperty("numFound")
    private long totalResults;

    @JsonProperty("start")
    private long start;

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
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
