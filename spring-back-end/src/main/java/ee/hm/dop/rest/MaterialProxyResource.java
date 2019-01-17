package ee.hm.dop.rest;

import ee.hm.dop.service.proxy.MaterialProxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialProxy materialProxy;

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getProxyUrl(@QueryParam("id") Long id, @QueryParam("url") String url_param) throws IOException {
        if (StringUtils.isBlank(url_param) || url_param.equals("undefined")) {
            return Response.noContent().build();
        }
        try {
            return materialProxy.getProxyUrl(id, url_param);
        } catch (Exception e) {
            logger.error("getProxyUrl caused error for LearningObject {}, url {}, error {}. Returning no content", id, url_param, e.getMessage(), e);
            return Response.noContent().build();
        }
    }
}
