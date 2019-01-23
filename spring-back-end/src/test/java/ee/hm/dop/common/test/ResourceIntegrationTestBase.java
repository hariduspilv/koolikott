package ee.hm.dop.common.test;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.User;
import ee.hm.dop.rest.content.MaterialResourceTest;
import ee.hm.dop.rest.content.PortfolioResourceTest;
import ee.hm.dop.rest.filter.SecurityFilter;
import ee.hm.dop.service.login.dto.UserStatus;
import ee.hm.dop.service.reviewmanagement.dto.StatisticsFilterDto;
import org.apache.commons.configuration2.Configuration;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
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

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

//import org.glassfish.jersey.filter.LoggingFilter;

/**
 * Base class for all resource integration tests.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class ResourceIntegrationTestBase implements BaseClassForTests {

    public static final String DEV_LOGIN = "dev/login/";
    public static final String LOGOUT = "user/logout";
    private String RESOURCE_BASE_URL;
    @Inject
    protected Environment environment;
    @Inject
    protected Configuration configuration;
    protected AuthenticationFilter authenticationFilter;

    protected User login(TestUser testUser) {
        return login(testUser.idCode);
    }

    private User login(String idCode) {
        if (authenticationFilter != null) {
            logout();
        }
        AuthenticatedUser authenticatedUser = doGet(DEV_LOGIN + idCode, new GenericType<UserStatus>() {
        }).getAuthenticatedUser();

        assertNotNull("Login failed", authenticatedUser.getToken());
        assertNotNull("Login failed", authenticatedUser.getUser().getUsername());

        authenticationFilter = new AuthenticationFilter(authenticatedUser);
        return authenticatedUser.getUser();
    }

    @After
    public void logout() {
        if (authenticationFilter != null) {
            Response response = doPost(LOGOUT);
            if (SecurityFilter.HTTP_AUTHENTICATION_TIMEOUT == response.getStatus()) {
                //ignored as test user has already logged out
                //tests have same user logging in and out multiple times
            } else {
                assertEquals("Logout failed", Status.OK.getStatusCode(), response.getStatus());
            }
            authenticationFilter = null;
        }
    }

    public Portfolio getPortfolio(long id) {
        return doGet(format(PortfolioResourceTest.GET_PORTFOLIO_URL, id), Portfolio.class);
    }

    public Material getMaterial(long id) {
        return doGet(format(MaterialResourceTest.GET_MATERIAL_URL, id), Material.class);
    }

    public Material createOrUpdateMaterial(Material updatedMaterial) {
        if (updatedMaterial.getId() == null) {
            return doPost(MaterialResourceTest.CREATE_MATERIAL_URL, updatedMaterial, Material.class);
        }
        return doPost(MaterialResourceTest.UPDATE_MATERIAL_URL, updatedMaterial, Material.class);
    }

    protected <T> T doGet(String url, Class<? extends T> clazz) {
        return doGet(url, MediaType.APPLICATION_JSON_TYPE, clazz);
    }

    protected <T> T doGet(String url, MediaType mediaType, Class<? extends T> clazz) {
        Response response = doGet(url, mediaType);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        return response.readEntity(clazz);
    }

    protected <T> T doGet(String url, GenericType<T> genericType) {
        return doGet(url, MediaType.APPLICATION_JSON_TYPE, genericType);
    }

    protected <T> T doGet(String url, MediaType mediaType, GenericType<T> genericType) {
        Response response = doGet(url, mediaType);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        return response.readEntity(genericType);
    }

    protected Response doGet(String url) {
        return doGet(url, MediaType.APPLICATION_JSON_TYPE);
    }

    protected Response doGet(String url, MediaType mediaType) {
        return getTarget(url).request().accept(mediaType).get(Response.class);
    }

    protected Response doGet(String url, MultivaluedMap<String, Object> headers, MediaType mediaType) {
        return getTarget(url).request().headers(headers).accept(mediaType).get(Response.class);
    }

    protected <T> T doPost(String url, Object json, Class<? extends T> responseClass) {
        Response response = doPost(url, Entity.entity(json, MediaType.APPLICATION_JSON_TYPE), MediaType.APPLICATION_JSON_TYPE);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        return response.readEntity(responseClass);
    }

    protected Response doPost(String url, Entity<?> requestEntity) {
        return doPost(url, requestEntity, MediaType.APPLICATION_JSON_TYPE);
    }

    protected Response doPost(String url) {
        return doPost(url, new StatisticsFilterDto());
    }

    protected Response doPost(String url, Object json) {
        return doPost(url, Entity.json(json), MediaType.APPLICATION_JSON_TYPE);
    }

    protected Response doPost(String url, Entity<?> requestEntity, MediaType mediaType) {
        return getTarget(url).request().accept(mediaType).post(requestEntity);
    }

    protected <T> T doPut(String url, Object json, Class<? extends T> clazz) {
        Response response = doPut(url, Entity.json(json), MediaType.APPLICATION_JSON_TYPE);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        return response.readEntity(clazz);
    }

    protected Response doPut(String url, Object json) {
        return doPut(url, Entity.json(json), MediaType.APPLICATION_JSON_TYPE);
    }

    protected Response doPut(String url, Entity<?> requestEntity, MediaType mediaType) {
        return getTarget(url).request().accept(mediaType).put(requestEntity);
    }

    protected WebTarget getTarget(String url) {
        return getTarget(url, authenticationFilter);
    }

    protected WebTarget getTarget(String url, ClientRequestFilter clientRequestFilter) {
        return getClient(clientRequestFilter).target(getFullURL(url));
    }

    private Client getClient(ClientRequestFilter clientRequestFilter) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.READ_TIMEOUT, 60000); // ms
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 60000); // ms
        clientConfig.property(ClientProperties.FOLLOW_REDIRECTS, false);
        clientConfig.register(MultiPartFeature.class);
        clientConfig.register(LoggingFeature.class);

        Client client = ClientBuilder.newClient(clientConfig);
//                .property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_CLIENT, Level.FINE.getName());
        client.register(JacksonFeature.class);
        if (clientRequestFilter != null) {
            client.register(clientRequestFilter);
        }

        return client;
    }

    private String getFullURL(String path) {
        if (RESOURCE_BASE_URL == null) {
            //String property = environment.getProperty("server.port");
            RESOURCE_BASE_URL = format("http://localhost:%s/rest/", 1986);
        }
        return RESOURCE_BASE_URL + path;
    }

    @Provider
    public static class AuthenticationFilter implements ClientRequestFilter {
        private String token;
        private String username;

        public AuthenticationFilter(AuthenticatedUser authenticatedUser) {
            this.token = authenticatedUser.getToken();
            this.username = authenticatedUser.getUser().getUsername();
        }

        @Override
        public void filter(ClientRequestContext requestContext) {
            if (token != null && username != null) {
                requestContext.getHeaders().put("Authentication", asList(token));
                requestContext.getHeaders().put("Username", asList(username));
            }
        }
    }

    public Tag tag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }

    public User getUser(TestUser testUser) {
        return doGet("user?username=" + testUser.username, User.class);
    }
}
