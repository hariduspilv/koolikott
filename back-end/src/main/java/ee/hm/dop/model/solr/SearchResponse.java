package ee.hm.dop.model.solr;

import java.util.Map;

public class SearchResponse {

    private Response response;
    private ResponseHeader responseHeader;
    private String status;
    private Map<String, String> statusMessages;

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
