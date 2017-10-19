package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import ee.hm.dop.service.atom.AtomFeedService;
import org.apache.abdera.model.Feed;

/**
 * Created by joonas on 29.12.16.
 */

@Path("/")
public class AtomFeedResource extends BaseResource{

    @Inject
    private AtomFeedService atomFeedService;

    @GET
    @Path("{lang}/feed")
    @Produces("application/atom+xml")
    public Feed getMaterials(@PathParam("lang") String lang) {
        return atomFeedService.getFeed(lang);
    }
}
