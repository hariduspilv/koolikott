package ee.hm.dop.common.test;

import static ee.hm.dop.utils.ConfigurationProperties.SERVER_PORT;
import static java.lang.String.format;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration.Configuration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.google.inject.Inject;

/**
 * Base class for all resource integration tests.
 */
public abstract class ResourceIntegrationTestBase extends IntegrationTestBase {

    private static String RESOURCE_BASE_URL;

    @Inject
    private static Configuration configuration;

    /*
     * GET
     */

    protected static <T> T doGet(String url, Class<? extends T> clazz) {
        return doGet(url, MediaType.APPLICATION_JSON_TYPE, clazz);
    }

    protected static <T> T doGet(String url, MediaType mediaType, Class<? extends T> clazz) {
        Response response = doGet(url, mediaType);
        return response.readEntity(clazz);
    }

    protected static <T> T doGet(String url, GenericType<T> genericType) {
        return doGet(url, MediaType.APPLICATION_JSON_TYPE, genericType);
    }

    protected static <T> T doGet(String url, MediaType mediaType, GenericType<T> genericType) {
        Response response = doGet(url, mediaType);
        return response.readEntity(genericType);
    }

    protected static Response doGet(String url) {
        return doGet(url, MediaType.APPLICATION_JSON_TYPE);
    }

    protected static Response doGet(String url, MediaType mediaType) {
        Response response = getTarget(url).request().accept(mediaType).get(Response.class);
        return response;
    }

    /*
     * Target
     */

    protected static WebTarget getTarget(String url) {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.READ_TIMEOUT, 60000); // ms
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 60000); // ms

        Client client = ClientBuilder.newClient(clientConfig);
        client.register(JacksonFeature.class);
        client.register(LoggingFilter.class);

        return client.target(getFullURL(url));
    }

    private static String getFullURL(String path) {
        if (RESOURCE_BASE_URL == null) {
            String port = configuration.getString(SERVER_PORT);
            RESOURCE_BASE_URL = format("http://localhost:%s/rest/", port);
        }

        return RESOURCE_BASE_URL + path;
    }
}
