package ee.hm.dop.service.solr;

import java.util.List;

import ee.hm.dop.model.solr.SearchResponse;

public interface SolrEngineService {

    SearchResponse search(String query, long start, String sort);

    SearchResponse search(String query, long start, String sort, long limit);

    List<String> suggest(String query, boolean suggestTags);

    void updateIndex();

}
