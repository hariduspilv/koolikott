package ee.hm.dop.rest.login;

import com.google.common.collect.Lists;
import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import ee.hm.dop.service.login.dto.UserStatus;
import org.flywaydb.core.internal.jdbc.TransactionTemplate;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_CLIENT_ID;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_URL_AUTHORIZE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Ignore
public class LoginResourceTest extends ResourceIntegrationTestBase {

    public static final String LOGIN_ID_CARD = "login/idCard";

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
        assertTrue(url.contains("eKoolUserMissingIdCode=true"));
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
        assertTrue(url.contains("stuudiumUserMissingIdCode=true"));
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

        assertNotNull(mobileIDSecurityCodes);
        assertNotNull(mobileIDSecurityCodes.getToken());
        assertNotNull(mobileIDSecurityCodes.getChallengeId());

        Response isValid = doGet(String.format("login/mobileId/isValid?token=%s", mobileIDSecurityCodes.getToken()));
        assertEquals(HttpStatus.OK.value(), isValid.getStatus());
        if (false) {
            //todo spring transactions
            UserStatus userStatus = isValid.readEntity(new GenericType<UserStatus>() {
            });
            assertNotNull(userStatus);
            AuthenticatedUser authenticatedUser = userStatus.getAuthenticatedUser();
            assertNotNull(authenticatedUser);
            assertNotNull(authenticatedUser.getToken());
            User user = authenticatedUser.getUser();
            assertNull(user.getIdCode());
            assertEquals("Matt", user.getName());
            assertEquals("Smith", user.getSurname());
            assertNotNull(user.getUsername());
        }
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
        assertEquals(HttpStatus.OK.value(), isValid.getStatus());
    }

    @Test
    public void mobileIDAuthenticateMissingResponseFields() {
        String phoneNumber = "+37233331234";
        String idCode = "44556677889";
        String language = LanguageC.EST;
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void mobileIDAuthenticateInvalidPhoneNumber() {
        String phoneNumber = "+3721";
        String idCode = "55667788990";
        String language = LanguageC.EST;
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void mobileIDAuthenticateNonEstonianPhoneNumber() {
        String phoneNumber = "+37077778888";
        String idCode = "66778899001";
        String language = LanguageC.ENG;
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void mobileIDIsAuthenticatedInvalidSessionCode() {
        String token = "2";
        Response isValid = doGet(String.format("login/mobileId/isValid?token=%s", token));
        assertEquals(HttpStatus.OK.value(), isValid.getStatus());
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
        public void filter(ClientRequestContext requestContext) {
            requestContext.getHeaders().put("SSL_CLIENT_S_DN", matiMaasikasInfo());
            requestContext.getHeaders().put("SSL_AUTH_VERIFY", newArrayList("SUCCESS"));
        }
    }

    @Provider
    public static class LoginFilter2 implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) {
            requestContext.getHeaders().put("SSL_CLIENT_S_DN", matiMaasikasInfo());
            requestContext.getHeaders().put("SSL_AUTH_VERIFY", newArrayList("FAILED"));
        }
    }

    @Provider
    public static class LoginFilterAccentInName implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            requestContext.getHeaders().put("SSL_CLIENT_S_DN", peeterPaanInfo());
            requestContext.getHeaders().put("SSL_AUTH_VERIFY", newArrayList("SUCCESS"));
        }
    }

    private static List<Object> matiMaasikasInfo() {
        return Lists.newArrayList(
                "serialNumber=39011220011",
                "GN=MATI",
                "SN=MAASIKAS",
                "CN=MAASIKAS\\,MATI\\,39011220011",
                "OU=authentication",
                "O=ESTEID",
                "C=EE");
    }

    private static List<Object> peeterPaanInfo() {
        return Lists.newArrayList(
                "serialNumber=55555555555",
                "GN=PEETER",
                "SN=PÄÄN",
                "CN=PÄÄN\\,PEETER\\,55555555555",
                "OU=authentication",
                "O=ESTEID",
                "C=EE");
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

    @Test
    public void haridAuthenticate_returns_temporary_redirect_status() {
        Response response = doGet("login/harid");
        assertEquals(Response.Status.TEMPORARY_REDIRECT.getStatusCode(), response.getStatus());
    }

    @Test
    public void haridAuthenticateSuccess() {
        Response response = doGet("login/harid/success?code=123456789");
        String url = response.getHeaderString("Location");
        assertEquals(true, url.contains("token"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void haridAuthenticateSuccessTwo() {
        Response response = doGet("login/harid/success?code=987654321");
        String url = response.getHeaderString("Location");
        assertTrue(url.contains("token"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void harid_authentication_without_id_code_returns_missin_id_message() {
        Response response = doGet("login/harid/success?code=123123456");
        String url = response.getHeaderString("Location");
        assertTrue(url.contains("harIdUserMissingIdCode=true"));
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void haridAuthenticateFail() {
        Response response = doGet("login/harid/success?code=000000");
        String url = response.getHeaderString("Location");
        assertFalse(url.contains("token"));
        assertEquals(307, response.getStatus());

        logout();
    }
}
