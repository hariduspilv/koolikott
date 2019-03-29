package ee.hm.dop.service.solr;

import ee.hm.dop.model.solr.SolrSearchResponse;

import java.util.List;

public interface SolrEngineService {

    SolrSearchResponse search(SolrSearchRequest searchRequest);

    SolrSearchResponse limitlessSearch(SolrSearchRequest searchRequest);

    List<String> suggest(String query);

    void fullImport();

    void updateIndex();

}
