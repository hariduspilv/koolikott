package ee.hm.dop.model.solr;

import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * Created by joonas on 31.08.16.
 */
public class DOPQueryResponse extends QueryResponse{

    private SearchResponse searchResponse;
    private QueryResponse queryResponse;

    public SearchResponse getSearchResponse() {
        return searchResponse;
    }

    public void setSearchResponse(SearchResponse searchResponse) {
        this.searchResponse = searchResponse;
    }

    public QueryResponse getQueryResponse() {
        return queryResponse;
    }

    public void setQueryResponse(QueryResponse queryResponse) {
        this.queryResponse = queryResponse;
    }

}
