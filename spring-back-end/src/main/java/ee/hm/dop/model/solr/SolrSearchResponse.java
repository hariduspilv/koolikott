package ee.hm.dop.model.solr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolrSearchResponse {

    private Response response;
    private Map<String, Response> grouped;
    private ResponseHeader responseHeader;
    private String status;
    private Map<String, String> statusMessages;
    private long exactResultCount;
    private long similarResultCount;

    public long getExactResultCount() {
        return exactResultCount;
    }

    public void setExactResultCount(long exactResultCount) {
        this.exactResultCount = exactResultCount;
    }

    public long getSimilarResultCount() {
        return similarResultCount;
    }

    public void setSimilarResultCount(long similarResultCount) {
        this.similarResultCount = similarResultCount;
    }

    public Map<String, Response> getGrouped() {
        return grouped;
    }

    public void setGrouped(Map<String, Response> grouped) {
        this.grouped = grouped;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getStatusMessages() {
        return statusMessages;
    }

    public void setStatusMessages(Map<String, String> statusMessages) {
        this.statusMessages = statusMessages;
    }

}
