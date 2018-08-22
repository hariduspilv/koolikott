package ee.hm.dop.rest.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class DopClientRequestFilter implements ClientRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(DopClientRequestFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        logger.info(requestContext.getUri().toString());
        if (requestContext.getEntity() != null) {
            logger.info(requestContext.getEntity().toString());
        }
    }
}
