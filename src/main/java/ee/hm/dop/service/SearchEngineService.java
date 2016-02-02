package ee.hm.dop.service;

import ee.hm.dop.model.solr.SearchResponse;

public interface SearchEngineService {

    public SearchResponse search(String query, long start, String sort);

    public SearchResponse search(String query, long start, long limit, String sort);

    public void updateIndex();

}
