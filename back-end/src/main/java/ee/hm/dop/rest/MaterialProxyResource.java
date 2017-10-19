package ee.hm.dop.rest;

import ee.hm.dop.service.proxy.MaterialProxy;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("material/externalMaterial")
public class MaterialProxyResource extends BaseResource {

    @Inject
    private MaterialProxy materialProxy;

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProxyUrl(@QueryParam("url") String url_param) throws IOException {
        if (StringUtils.isBlank(url_param)){
            return Response.noContent().build();
        }
        return materialProxy.getProxyUrl(url_param);
    }
}
