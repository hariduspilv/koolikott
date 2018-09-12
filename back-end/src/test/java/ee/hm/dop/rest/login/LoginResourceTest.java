package ee.hm.dop.rest.login;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import ee.hm.dop.service.login.dto.UserStatus;
import org.apache.commons.configuration2.Configuration;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Ignore;
import org.junit.Test;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import static ee.hm.dop.rest.login.LoginTestUtil.*;
import static ee.hm.dop.utils.ConfigurationProperties.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class LoginResourceTest extends ResourceIntegrationTestBase {

    public static final String LOGIN_ID_CARD = "login/idCard";
    @Context
    private HttpServletRequest request;

    @Test
    public void login() {
        AuthenticatedUser authenticatedUser = loginWithId(new LoginFilter1());
        assertNotNull(authenticatedUser.getToken());
    }

    @Test
    public void loginAuthenticationFailed() {
        AuthenticatedUser authenticatedUser = loginWithId(new LoginFilter2());
        assertNull(authenticatedUser);
    }

    @Test
    public void loginSameNameWithAccent() {
        AuthenticatedUser authenticatedUser = loginWithId(new LoginFilterAccentInName());
        assertNotNull(authenticatedUser.getToken());
        assertEquals("peeter.paan2", authenticatedUser.getUser().getUsername());
    }

    @Test
    public void ekoolAuthenticate_returns_temporary_redirect_status() throws Exception {
        Response response = doGet("login/ekool");
        assertEquals(Response.Status.TEMPORARY_REDIRECT.getStatusCode(), response.getStatus());
    }

    @Test
    public void ekoolAuthenticateSuccess() {
        Response response = doGet("login/ekool/success?code=123456789");
        String url = response.getHeaderString("Location");
        assertEquals(true, url.contains("token"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void ekoolAuthenticateSuccessTwo() {
        Response response = doGet("login/ekool/success?code=987654321");
        String url = response.getHeaderString("Location");
        assertTrue(url.contains("token"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void eKool_authentication_without_id_code_returns_missin_id_message() {
        Response response = doGet("login/ekool/success?code=123123456");
        String url = response.getHeaderString("Location");
        assertTrue(url.contains("eKoolLoginMissingIdCode=true"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void ekoolAuthenticateFail() {
        Response response = doGet("login/ekool/success?code=000000");
        String url = response.getHeaderString("Location");
        assertFalse(url.contains("token"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void stuudiumAuthenticateSuccess() {
        Response response = doGet("login/stuudium?token=987654");
        String url = response.getHeaderString("Location");
        assertTrue(url.contains("token"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void stuudium_user_without_id_code_returns_missing_id_message() {
        Response response = doGet("login/stuudium?token=123223");
        String url = response.getHeaderString("Location");
        assertTrue(url.contains("stuudiumLoginMissingIdCode=true"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void stuudiumAuthenticateFail() {
        Response response = doGet("login/stuudium?token=000000");
        String url = response.getHeaderString("Location");

        assertFalse(url.contains("token"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void getAuthenticatedUser() {
        String token = "token";
        Response response = doGet("login/getAuthenticatedUser?token=" + token);
        AuthenticatedUser authenticatedUser = response.readEntity(new GenericType<AuthenticatedUser>() {
        });
        assertNotNull(authenticatedUser);
        assertEquals("token", authenticatedUser.getToken());
    }

    @Test
    public void getAuthenticatedUserWrongToken() {
        String token = "wrongToken";
        Response response = doGet("login/getAuthenticatedUser?token=" + token);
        AuthenticatedUser authenticatedUser = response.readEntity(new GenericType<AuthenticatedUser>() {
        });
        assertNull(authenticatedUser);
    }

    @Test
    public void mobileIDAuthenticate() {
        String phoneNumber = "+37255551234";
        String idCode = "22334455667";
        String language = LanguageC.EST;
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        MobileIDSecurityCodes mobileIDSecurityCodes = response.readEntity(new GenericType<MobileIDSecurityCodes>() {
        });

        assertNotNull(mobileIDSecurityCodes.getToken());
        assertNotNull(mobileIDSecurityCodes.getChallengeId());

        Response isValid = doGet(String.format("login/mobileId/isValid?token=%s", mobileIDSecurityCodes.getToken()));
        AuthenticatedUser authenticatedUser = isValid.readEntity(new GenericType<UserStatus>() {
        }).getAuthenticatedUser();

        assertNotNull(authenticatedUser.getToken());
        User user = authenticatedUser.getUser();
        assertNull(user.getIdCode());
        assertEquals("Matt", user.getName());
        assertEquals("Smith", user.getSurname());
        assertNotNull(user.getUsername());
    }

    @Test
    public void mobileIDAuthenticateNotValid() {
        String phoneNumber = "+37244441234";
        String idCode = "33445566778";
        String language = LanguageC.EST;
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        MobileIDSecurityCodes mobileIDSecurityCodes = response.readEntity(new GenericType<MobileIDSecurityCodes>() {
        });

        assertNotNull(mobileIDSecurityCodes.getToken());
        assertNotNull(mobileIDSecurityCodes.getChallengeId());

        Response isValid = doGet(String.format("login/mobileId/isValid?token=%s", mobileIDSecurityCodes.getToken()));
        assertEquals(204, isValid.getStatus());
    }

    @Test
    public void mobileIDAuthenticateMissingResponseFields() {
        String phoneNumber = "+37233331234";
        String idCode = "44556677889";
        String language = LanguageC.EST;
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        assertEquals(204, response.getStatus());
    }

    @Test
    public void mobileIDAuthenticateInvalidPhoneNumber() {
        String phoneNumber = "+3721";
        String idCode = "55667788990";
        String language = LanguageC.EST;
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        assertEquals(204, response.getStatus());
    }

    @Test
    public void mobileIDAuthenticateNonEstonianPhoneNumber() {
        String phoneNumber = "+37077778888";
        String idCode = "66778899001";
        String language = LanguageC.ENG;
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        assertEquals(204, response.getStatus());
    }

    @Test
    public void mobileIDIsAuthenticatedInvalidSessionCode() {
        String token = "2";
        Response isValid = doGet(String.format("login/mobileId/isValid?token=%s", token));
        assertEquals(204, isValid.getStatus());
    }

    @Test
    public void stuudiumAuthenticate() {
        Response response = doGet("login/stuudium");
        String url = response.getHeaderString("Location");
        assertEquals(307, response.getStatus());
        assertEquals(
                configuration.getString(STUUDIUM_URL_AUTHORIZE) + "client_id="
                        + configuration.getString(STUUDIUM_CLIENT_ID), url);
    }

    @Provider
    public static class LoginFilter1 implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            List<Object> list1 = new ArrayList<>();
            list1.add("serialNumber=39011220011");
            list1.add("GN=MATI");
            list1.add("SN=MAASIKAS");
            list1.add("CN=MATI,MAASIKAS,39011220011");
            list1.add("OU=authentication");
            list1.add("O=ESTEID");
            list1.add("C=EE");
            requestContext.getHeaders().put("SSL_CLIENT_S_DN", list1);

            List<Object> list2 = new ArrayList<>();
            list2.add("SUCCESS");
            requestContext.getHeaders().put("SSL_AUTH_VERIFY", list2);
        }
    }

    @Provider
    public static class LoginFilter2 implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            List<Object> list1 = new ArrayList<>();
            list1.add("serialNumber=39011220011");
            list1.add("GN=MATI");
            list1.add("SN=MAASIKAS");
            list1.add("CN=MATI,MAASIKAS,39011220011");
            list1.add("OU=authentication");
            list1.add("O=ESTEID");
            list1.add("C=EE");
            requestContext.getHeaders().put("SSL_CLIENT_S_DN", list1);

            List<Object> list2 = new ArrayList<>();
            list2.add("FAILED");
            requestContext.getHeaders().put("SSL_AUTH_VERIFY", list2);
        }
    }

    @Provider
    public static class LoginFilterAccentInName implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            List<Object> list1 = new ArrayList<>();
            list1.add("serialNumber=55555555555");
            list1.add("GN=PEETER");
            list1.add("SN=PÄÄN");
            list1.add("CN=PEETER,PÄÄN,55555555555");
            list1.add("OU=authentication");
            list1.add("O=ESTEID");
            list1.add("C=EE");
            requestContext.getHeaders().put("SSL_CLIENT_S_DN", list1);

            List<Object> list2 = new ArrayList<>();
            list2.add("SUCCESS");
            requestContext.getHeaders().put("SSL_AUTH_VERIFY", list2);
        }
    }

    private String encodeQuery(String query) {
        try {
            return URLEncoder.encode(query, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthenticatedUser loginWithId(ClientRequestFilter filter) {
        UserStatus status = getTarget(LOGIN_ID_CARD, filter).request().accept(MediaType.APPLICATION_JSON).get(UserStatus.class);
        return status != null ? status.getAuthenticatedUser() : null;
    }
}
