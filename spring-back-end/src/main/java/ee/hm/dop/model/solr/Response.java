package ee.hm.dop.model.solr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    @JsonProperty("doclist")
    private Response groupResponse;

    @JsonProperty("docs")
    private List<Document> documents;

    @JsonProperty("numFound")
    private long totalResults;

    @JsonProperty("start")
    private long start;

    @JsonProperty("matches")
    private long matches;

    public long getMatches() {
        return matches;
    }

    public void setMatches(long matches) {
        this.matches = matches;
    }

    public Response getGroupResponse() {
        return groupResponse;
    }

    public void setGroupResponse(Response groupResponse) {
        this.groupResponse = groupResponse;
    }

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
