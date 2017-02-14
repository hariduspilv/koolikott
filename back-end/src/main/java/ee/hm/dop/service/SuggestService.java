package ee.hm.dop.service;

import static ee.hm.dop.service.SolrService.getTokenizedQueryString;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.response.Suggestion;

/**
 * Created by joonas on 16.08.16.
 */
public class SuggestService {

    @Inject
    SolrEngineService solrEngineService;

    public Response suggest(String query, boolean suggestTags){

        if(query.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> suggestResponse = doSuggest(query, suggestTags);

        return Response.ok(suggestResponse).build();
    }

    private List<String> doSuggest(String query, boolean suggestTags){
        return solrEngineService.suggest(query, suggestTags);
    }

}
