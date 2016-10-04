package ee.hm.dop.service;

import static ee.hm.dop.service.SolrService.getTokenizedQueryString;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.response.SpellCheckResponse;

/**
 * Created by joonas on 16.08.16.
 */
public class SuggestService {

    @Inject
    SolrEngineService solrEngineService;

    public Response suggest(String query){

        if(query.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        SpellCheckResponse.Suggestion suggestResponse = doSuggest(query);

        return Response.ok(suggestResponse).build();
    }

    private SpellCheckResponse.Suggestion doSuggest(String query){
        String queryString = getTokenizedQueryString(query);

        return solrEngineService.suggest(queryString);
    }

}
