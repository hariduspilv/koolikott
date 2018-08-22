package ee.hm.dop.rest.filter;

import ee.hm.dop.utils.ConfigurationProperties;
import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static ee.hm.dop.config.guice.GuiceInjector.getInjector;

@Provider
public class DopClientRequestFilter implements ClientRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(DopClientRequestFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (configuration().getBoolean(ConfigurationProperties.REQUEST_EXTRA_LOGGING)) {
            logger.info(requestContext.getUri().toString());
            if (requestContext.getEntity() != null) {
                logger.info(requestContext.getEntity().toString());
            }
        }
    }

    protected Configuration configuration() {
        return getInjector().getInstance(Configuration.class);
    }
}
