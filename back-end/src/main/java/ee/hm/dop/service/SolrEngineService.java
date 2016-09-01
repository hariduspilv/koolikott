package ee.hm.dop.service;

import ee.hm.dop.model.solr.SearchResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;

public interface SolrEngineService {

    public SearchResponse search(String query, long start, String sort);

    public SearchResponse search(String query, long start, long limit, String sort);

    public SpellCheckResponse.Suggestion suggest(String query);

    public void updateIndex();

}
