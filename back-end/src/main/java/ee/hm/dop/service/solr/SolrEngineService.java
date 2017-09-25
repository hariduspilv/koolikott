package ee.hm.dop.service.solr;

import java.util.List;

import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.service.SuggestionStrategy;

public interface SolrEngineService {

    SearchResponse search(String query, long start, String sort);

    SearchResponse search(String query, long start, String sort, long limit);

    List<String> suggest(String query, SuggestionStrategy suggestionStrategy);

    void updateIndex();

}
