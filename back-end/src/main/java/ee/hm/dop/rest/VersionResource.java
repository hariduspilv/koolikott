package ee.hm.dop.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/version")
public class VersionResource extends BaseResource{

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getVersion() {
        return "1.32.0";
    }
}
