package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import ee.hm.dop.service.solr.SolrEngineService;

/**
 * Created by joonas on 16.08.16.
 */
public class SuggestService {

    @Inject
    private SolrEngineService solrEngineService;

    public Response suggest(String query, boolean suggestTags) {
        if (query.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<String> suggestResponse = doSuggest(query, suggestTags);

        return Response.ok(suggestResponse).build();
    }

    private List<String> doSuggest(String query, boolean suggestTags) {
        return solrEngineService.suggest(query, suggestTags);
    }

}
