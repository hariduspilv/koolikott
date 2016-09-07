package ee.hm.dop.common.test;

import static ee.hm.dop.utils.ConfigurationProperties.SERVER_PORT;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.google.inject.Inject;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import org.apache.commons.configuration.Configuration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.After;

/**
 * Base class for all resource integration tests.
 */
public abstract class ResourceIntegrationTestBase extends IntegrationTestBase {

    private static String RESOURCE_BASE_URL;

    @Inject
    private static Configuration configuration;

    private static AuthenticationFilter authenticationFilter;

    protected User login(String idCode) {
        Response response = doGet("dev/login/" + idCode);
        AuthenticatedUser authenticatedUser = response.readEntity(new GenericType<AuthenticatedUser>() {
        });

        assertNotNull("Login failed", authenticatedUser.getToken());
        assertNotNull("Login failed", authenticatedUser.getUser().getUsername());

        authenticationFilter = new AuthenticationFilter(authenticatedUser);

        return authenticatedUser.getUser();
    }

    @After
    public void logout() {
        if (authenticationFilter != null) {
            Response response = getTarget("logout", authenticationFilter).request()
                    .accept(MediaType.APPLICATION_JSON_TYPE).post(null);

            assertEquals("Logout failed", Status.NO_CONTENT.getStatusCode(), response.getStatus());

            authenticationFilter = null;
        }
    }

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
        return getTarget(url).request().accept(mediaType).get(Response.class);
    }

    protected static Response doGet(String url, MultivaluedMap<String, Object> headers, MediaType mediaType) {
        return getTarget(url).request().headers(headers).accept(mediaType).get(Response.class);
    }

    /*
     * POST
     */

    protected static <T1, T2> T2 doPost(String url, T1 entity, Class<? extends T2> clazz) {
        Entity<?> requestEntity = Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE);
        return doPost(url, requestEntity, MediaType.APPLICATION_JSON_TYPE, clazz);
    }

    protected static <T> T doPost(String url, Entity<?> requestEntity, MediaType mediaType, Class<? extends T> clazz) {

        Response response = doPost(url, requestEntity, mediaType);
        return response.readEntity(clazz);
    }

    protected static Response doPost(String url, Entity<?> requestEntity) {
        return doPost(url, requestEntity, MediaType.APPLICATION_JSON_TYPE);
    }

    protected static Response doPost(String url, Entity<?> requestEntity, MediaType mediaType) {
        return getTarget(url).request().accept(mediaType).post(requestEntity);
    }

    protected static Response doPost(String url, ClientRequestFilter clientRequestFilter, Entity<?> requestEntity,
                                     MediaType mediaType) {
        return getTarget(url, clientRequestFilter).request().accept(mediaType).post(requestEntity);
    }

    /*
     * PUT
     */

    protected static <T> T doPut(String url, Entity<?> requestEntity, Class<? extends T> clazz) {
        Response response = doPut(url, requestEntity, MediaType.APPLICATION_JSON_TYPE);
        return response.readEntity(clazz);
    }

    protected static Response doPut(String url, Entity<?> requestEntity) {
        return doPut(url, requestEntity, MediaType.APPLICATION_JSON_TYPE);
    }

    protected static Response doPut(String url, Entity<?> requestEntity, MediaType mediaType) {
        return getTarget(url).request().accept(mediaType).put(requestEntity);
    }

    /*
     * DELETE
     */

    protected static Response doDelete(String url) {
        return getTarget(url).request().delete();
    }

    /*
     * Target
     */

    protected static WebTarget getTarget(String url) {
        return getTarget(url, authenticationFilter);
    }

    protected static WebTarget getTarget(String url, ClientRequestFilter clientRequestFilter) {
        return getClient(clientRequestFilter).target(getFullURL(url));
    }

    private static Client getClient(ClientRequestFilter clientRequestFilter) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.READ_TIMEOUT, 60000); // ms
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 60000); // ms
        clientConfig.property(ClientProperties.FOLLOW_REDIRECTS, false);
        clientConfig.register(MultiPartFeature.class);

        Client client = ClientBuilder.newClient(clientConfig);
        client.register(JacksonFeature.class);
        client.register(LoggingFilter.class);
        if (clientRequestFilter != null) {
            client.register(clientRequestFilter);
        }

        return client;
    }

    private static String getFullURL(String path) {
        if (RESOURCE_BASE_URL == null) {
            String port = configuration.getString(SERVER_PORT);
            RESOURCE_BASE_URL = format("http://localhost:%s/rest/", port);
        }

        return RESOURCE_BASE_URL + path;
    }

    @Provider
    public static class AuthenticationFilter implements ClientRequestFilter {
        private String token = null;
        private String username = null;

        public AuthenticationFilter(AuthenticatedUser authenticatedUser) {
            this.token = authenticatedUser.getToken();
            this.username = authenticatedUser.getUser().getUsername();
        }

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {

            if (token != null && username != null) {
                List<Object> tokenList = new ArrayList<>();
                tokenList.add(token);
                requestContext.getHeaders().put("Authentication", tokenList);

                List<Object> usernameList = new ArrayList<>();
                usernameList.add(username);
                requestContext.getHeaders().put("Username", usernameList);
            }
        }
    }
}
