package ee.hm.dop.service.solr;

import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.service.SuggestionStrategy;

import java.util.List;

public interface SolrEngineService {

    SearchResponse search(SearchRequest searchRequest);

    SearchResponse limitlessSearch(SearchRequest searchRequest);

    List<String> suggest(String query, SuggestionStrategy suggestionStrategy);

    void updateIndex();

}
