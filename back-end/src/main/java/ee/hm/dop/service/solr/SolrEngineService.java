package ee.hm.dop.service.solr;

import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.service.SuggestionStrategy;

import java.util.List;

public interface SolrEngineService {

    SearchResponse search(String query, long start, String sort);

    SearchResponse search(String query, long start, String sort, boolean isSearchGrouped, String originalQuery);

    SearchResponse search(String query, long start, String sort, long limit);

    SearchResponse search(String query, long start, String sort, long limit, boolean isSearchGrouped, String originalQuery);

    List<String> suggest(String query, SuggestionStrategy suggestionStrategy);

    void updateIndex();

}
