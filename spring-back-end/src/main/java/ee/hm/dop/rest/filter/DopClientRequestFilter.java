package ee.hm.dop.rest.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.stream.Collectors;

@Provider
public class DopClientRequestFilter implements ClientRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(DopClientRequestFilter.class);

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String logString = "";
        if (requestContext.getUri() != null){
            logString += requestContext.getUri().getAuthority() + requestContext.getUri().getPath();
        }
        if (requestContext.getMethod() != null) {
            logString += "\n\tMETHOD: " + requestContext.getMethod();
        }
        if (requestContext.getUri() != null) {
            logString += "\n\tURL: " + requestContext.getUri().toString();
        }
        String headers = headers(requestContext);
        if (!headers.isEmpty()) {
            logString += "\n\tHEADERS: " + headers;
        }
        if (requestContext.hasEntity()) {
            logString += "\n\tENTITY: " + requestContext.getEntity().toString();
        }
        logger.info(logString);
    }

    private String headers(ClientRequestContext requestContext) {
        return requestContext.getHeaders().keySet().stream()
                .map(key -> key + ":" + requestContext.getHeaderString(key))
                .collect(Collectors.joining(", "));
    }

}
