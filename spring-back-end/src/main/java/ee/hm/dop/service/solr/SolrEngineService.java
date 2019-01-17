package ee.hm.dop.service.solr;

import ee.hm.dop.model.solr.SolrSearchResponse;
import ee.hm.dop.service.SuggestionStrategy;

import java.util.List;

public interface SolrEngineService {

    SolrSearchResponse search(SolrSearchRequest searchRequest);

    SolrSearchResponse limitlessSearch(SolrSearchRequest searchRequest);

    List<String> suggest(String query, SuggestionStrategy suggestionStrategy);

    void fullImport();

    void updateIndex();

}
