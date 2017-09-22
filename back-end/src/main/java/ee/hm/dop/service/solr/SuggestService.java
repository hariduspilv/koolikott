package ee.hm.dop.service.solr;

import ee.hm.dop.service.SuggestionStrategy;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * Created by joonas on 16.08.16.
 */
public class SuggestService {

    @Inject
    private SolrEngineService solrEngineService;

    public Response suggest(String query, SuggestionStrategy suggestionStrategy) {
        if (query.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<String> suggestResponse = solrEngineService.suggest(query, suggestionStrategy);
        return Response.ok(suggestResponse).build();
    }

}
