package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ee.hm.dop.service.SuggestService;
import org.apache.solr.client.solrj.response.SpellCheckResponse;

/**
 * Created by joonas on 15.08.16.
 */

@Path("suggest")
public class SuggestResource extends BaseResource {

    @Inject
    SuggestService suggestService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response suggest(@QueryParam("q") String query){
        return suggestService.suggest(query);
    }
}
