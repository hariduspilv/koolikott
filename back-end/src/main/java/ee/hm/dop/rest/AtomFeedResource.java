package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import ee.hm.dop.service.AtomFeedService;
import org.apache.abdera.model.Feed;

/**
 * Created by joonas on 29.12.16.
 */

@Path("/")
public class AtomFeedResource {

    @Inject
    private AtomFeedService atomFeedService;

    @GET
    @Path("et/feed")
    @Produces("application/atom+xml")
    public Feed getMaterialsET() {
        return atomFeedService.getFeed("est");
    }

    @GET
    @Path("en/feed")
    @Produces("application/atom+xml")
    public Feed getMaterialsEN() {
        return atomFeedService.getFeed("eng");

    }

    @GET
    @Path("ru/feed")
    @Produces("application/atom+xml")
    public Feed getMaterialsRU() {
        return atomFeedService.getFeed("rus");
    }
}
