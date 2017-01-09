package ee.hm.dop.service;

import java.util.List;

import ee.hm.dop.model.solr.SearchResponse;

public interface SolrEngineService {

    public SearchResponse search(String query, long start, String sort);

    public SearchResponse search(String query, long start, long limit, String sort);

    public List<String> suggest(String query, boolean suggestTags);

    public void updateIndex();

}
