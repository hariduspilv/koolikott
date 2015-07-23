package ee.hm.dop.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    private Response response;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        @JsonProperty("docs")
        private List<Document> documents;
        
        @JsonProperty("numFound")
        private long resultCount;

        public List<Document> getDocuments() {
            return documents;
        }

        public void setDocuments(List<Document> documents) {
            this.documents = documents;
        }
        
        public long getResultCount() {
            return resultCount;
        }
        
        public void setResultCount(long resultCount) {
            this.resultCount = resultCount;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {

        private long id;

        public long getId() {
            return id;
        }

        public void setId(String id) {
            this.id = Long.parseLong(id);
        }
    }
}
