package ee.hm.dop.rest;

import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_CLIENT_ID;
import static ee.hm.dop.utils.ConfigurationProperties.STUUDIUM_URL_AUTHORIZE;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_ASSERTION_CONSUMER_SERVICE_INDEX;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_CONNECTION_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_SSO;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.User;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import org.apache.commons.configuration.Configuration;
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

public class LoginResourceTest extends ResourceIntegrationTestBase {

    @Inject
    private Configuration configuration;

    @Inject
    private AuthenticationStateDao authenticationStateDao;

    @Context
    private HttpServletRequest request;

    @Test
    public void login() {
        AuthenticatedUser authenticatedUser = getTarget("login/idCard", new LoginFilter1()).request()
                .accept(MediaType.APPLICATION_JSON).get(AuthenticatedUser.class);
        assertNotNull(authenticatedUser.getToken());
    }

    @Test
    public void loginAuthenticationFailed() {
        AuthenticatedUser authenticatedUser = getTarget("login/idCard", new LoginFilter2()).request()
                .accept(MediaType.APPLICATION_JSON).get(AuthenticatedUser.class);
        assertNull(authenticatedUser);
    }

    @Test
    public void loginSameNameWithAccent() {
        AuthenticatedUser authenticatedUser = getTarget("login/idCard", new LoginFilterAccentInName()).request()
                .accept(MediaType.APPLICATION_JSON).get(AuthenticatedUser.class);
        assertNotNull(authenticatedUser.getToken());
        assertEquals("peeter.paan2", authenticatedUser.getUser().getUsername());
    }

    @Ignore("TAAT is not used currently and the test is unstable")
    @Test
    public void makeTaatRequest() throws Exception {
        Response response = doGet("login/taat");
        assertNotNull(response);

        String location = response.getHeaderString("Location");

        URI locationURI = new URI(location);
        String path = locationURI.getPath();
        String host = locationURI.getHost();
        String scheme = locationURI.getScheme();
        String actualTaatSSO = scheme + "://" + host + path;
        assertEquals(actualTaatSSO, configuration.getString(TAAT_SSO));

        String samlRequest = null;
        String token = null;
        String signature = null;
        String signatureAlgorithm = null;

        List<NameValuePair> parameters = URLEncodedUtils.parse(new URI(location), "UTF-8");
        for (NameValuePair parameter : parameters) {
            switch (parameter.getName()) {
                case "SAMLRequest":
                    samlRequest = parameter.getValue();
                    break;
                case "RelayState":
                    token = parameter.getValue();
                    break;
                case "Signature":
                    signature = parameter.getValue();
                    break;
                case "SigAlg":
                    signatureAlgorithm = parameter.getValue();
                    break;
                default:
                    fail("Unexpected parameter in request URL.");
                    break;
            }
        }

        assertNotNull(samlRequest);

        AuthnRequest authnRequest = decodeAuthnRequest(samlRequest);
        assertNotNull(authnRequest);
        assertEquals(configuration.getString(TAAT_CONNECTION_ID), authnRequest.getIssuer().getValue());

        Interval interval = new Interval(authnRequest.getIssueInstant(), new DateTime());
        assertFalse(authnRequest.getIssueInstant().isAfterNow());
        assertTrue(interval.toDurationMillis() < 10000);

        assertEquals(Integer.valueOf(configuration.getString(TAAT_ASSERTION_CONSUMER_SERVICE_INDEX)),
                authnRequest.getAssertionConsumerServiceIndex());

        assertNotNull(signature);
        assertNotNull(signatureAlgorithm);

        AuthenticationState authenticationState = authenticationStateDao.findAuthenticationStateByToken(token);
        assertNotNull(authenticationState);

        authenticationStateDao.delete(authenticationState);
    }

    @Test
    public void authenticateNoSAMLResponse() {
        MultivaluedMap<String, String> formParams = new MultivaluedStringMap();
        formParams.add("key", "value");

        Response response = doPost("login/taat", Entity.entity(formParams, MediaType.WILDCARD_TYPE));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void authenticateWrongData() {
        MultivaluedMap<String, String> formParams = new MultivaluedStringMap();
        formParams.add("SAMLResponse", "wrongResponse");

        Response response = doPost("login/taat", Entity.entity(formParams, MediaType.WILDCARD_TYPE));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void authenticateNoRelayState() {
        MultivaluedMap<String, String> formParams = new MultivaluedStringMap();
        formParams.add("SAMLResponse", getSAMLResponse());

        Response response = doPost("login/taat", Entity.entity(formParams, MediaType.WILDCARD_TYPE));
        assertNull(response.getHeaderString("Location"));
    }

    @Test
    public void authenticateWrongRelayStateToken() {
        MultivaluedMap<String, String> formParams = new MultivaluedStringMap();
        formParams.add("SAMLResponse", getSAMLResponse());
        formParams.add("RelayState", "WRONGTOKEN");

        Response response = doPost("login/taat", Entity.entity(formParams, MediaType.WILDCARD_TYPE));
        assertNull(response.getHeaderString("Location"));
    }

    @Test
    public void taatAuthenticate() {
        MultivaluedMap<String, String> formParams = new MultivaluedStringMap();
        formParams.add("SAMLResponse", getSAMLResponse());
        formParams.add("RelayState", "taatAuthenticateTestToken");

        Response response = doPost("login/taat", Entity.entity(formParams, MediaType.WILDCARD_TYPE));
        String url = response.getHeaderString("Location");
        String[] tokens = url.split("=");
        tokens = tokens[0].split("\\/");
        assertEquals(307, response.getStatus());
        assertEquals("loginRedirect?token", tokens[4]);
    }

    @Test
    public void ekoolAuthenticateSuccess() {
        Response response = doGet("login/ekool/success?code=123456789");
        String url = response.getHeaderString("Location");
        boolean hasToken = false;
        if (url.indexOf("token") != -1) {
            hasToken = true;
        }
        assertEquals(true, hasToken);
        assertEquals(307, response.getStatus());

        logout();

    }

    @Test
    public void ekoolAuthenticateSuccessTwo() {

        Response response = doGet("login/ekool/success?code=987654321");
        String url = response.getHeaderString("Location");

        boolean hasToken = false;
        if (url.indexOf("token") != -1) {
            hasToken = true;
        }
        assertEquals(true, hasToken);
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void ekoolAuthenticateFail() {

        Response response = doGet("login/ekool/success?code=000000");
        String url = response.getHeaderString("Location");

        boolean hasToken = false;
        if (url.indexOf("token") != -1) {
            hasToken = true;
        }
        assertEquals(false, hasToken);
        assertEquals(307, response.getStatus());

    }

    @Test
    public void stuudiumAuthenticateSuccess() {
        Response response = doGet("login/stuudium?token=987654");
        String url = response.getHeaderString("Location");

        boolean hasToken = false;
        if (url.contains("token")) {
            hasToken = true;
        }

        assertEquals(true, hasToken);
        assertEquals(307, response.getStatus());

        logout();
    }

    @Test
    public void stuudiumAuthenticateFail() {
        Response response = doGet("login/stuudium?token=000000");
        String url = response.getHeaderString("Location");

        boolean hasToken = false;
        if (url.indexOf("token") != -1) {
            hasToken = true;
        }

        assertEquals(false, hasToken);
        assertEquals(307, response.getStatus());
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
        String language = "est";
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        MobileIDSecurityCodes mobileIDSecurityCodes = response.readEntity(new GenericType<MobileIDSecurityCodes>() {
        });

        assertNotNull(mobileIDSecurityCodes.getToken());
        assertNotNull(mobileIDSecurityCodes.getChallengeId());

        Response isValid = doGet(String.format("login/mobileId/isValid?token=%s", mobileIDSecurityCodes.getToken()));
        AuthenticatedUser authenticatedUser = isValid.readEntity(new GenericType<AuthenticatedUser>() {
        });

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
        String language = "est";
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
        String language = "est";
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        assertEquals(204, response.getStatus());
    }

    @Test
    public void mobileIDAuthenticateInvalidPhoneNumber() {
        String phoneNumber = "+3721";
        String idCode = "55667788990";
        String language = "est";
        Response response = doGet(String.format("login/mobileId?phoneNumber=%s&idCode=%s&language=%s",
                encodeQuery(phoneNumber), idCode, language));
        assertEquals(204, response.getStatus());
    }

    @Test
    public void mobileIDAuthenticateNonEstonianPhoneNumber() {
        String phoneNumber = "+37077778888";
        String idCode = "66778899001";
        String language = "eng";
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

    private AuthnRequest decodeAuthnRequest(String request) throws Exception {
        InputStream inputStream = base64DecodeAndInflate(request);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(inputStream);
        Element element = document.getDocumentElement();

        UnmarshallerFactory unmarshallerFactory = org.opensaml.xml.Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        XMLObject authnRequest = unmarshaller.unmarshall(element);
        return (AuthnRequest) authnRequest;
    }

    private InputStream base64DecodeAndInflate(String data) throws Exception {
        byte[] base64DecodedResponse = Base64.decode(data);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Inflater decompresser = new Inflater(true);
        InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(stream, decompresser);
        inflaterOutputStream.write(base64DecodedResponse);
        inflaterOutputStream.close();
        return new ByteArrayInputStream(stream.toByteArray());
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

    private String getSAMLResponse() {
        return "PHNhbWxwOlJlc3BvbnNlIHhtbG5zOnNhbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6cHJvdG9jb2wiIHhtbG5zOnNhbWw9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb24iIElEPSJfOTk1OWJmZDIyZDE0NWRmNWY3MTAxOTNjNmM0ZTdlMWQwOTBmNTU4MmM3IiBWZXJzaW9uPSIyLjAiIElzc3VlSW5zdGFudD0iMjAxNS0wOS0wNFQxMTo1ODozMVoiIERlc3RpbmF0aW9uPSJodHRwczovL2xvY2FsaG9zdC9yZXN0L2xvZ2luL3RhYXQiIEluUmVzcG9uc2VUbz0iY2N1MzB0OGFmYjBvYnRsbG1rMWlvNDBkOWgiPjxzYW1sOklzc3Vlcj5odHRwczovL3Jlb3MudGFhdC5lZHUuZWUvc2FtbDIvaWRwL21ldGFkYXRhLnBocDwvc2FtbDpJc3N1ZXI+PGRzOlNpZ25hdHVyZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+CiAgPGRzOlNpZ25lZEluZm8+PGRzOkNhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz4KICAgIDxkczpTaWduYXR1cmVNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjcnNhLXNoYTEiLz4KICA8ZHM6UmVmZXJlbmNlIFVSST0iI185OTU5YmZkMjJkMTQ1ZGY1ZjcxMDE5M2M2YzRlN2UxZDA5MGY1NTgyYzciPjxkczpUcmFuc2Zvcm1zPjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIvPjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48L2RzOlRyYW5zZm9ybXM+PGRzOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNzaGExIi8+PGRzOkRpZ2VzdFZhbHVlPmYvWnIrTzFPWnpsRDBFTmxjcUVyZXQyaWluYz08L2RzOkRpZ2VzdFZhbHVlPjwvZHM6UmVmZXJlbmNlPjwvZHM6U2lnbmVkSW5mbz48ZHM6U2lnbmF0dXJlVmFsdWU+WGFYMGo2a2pVMDJVRHZpT1ZKbUF1Wk11dlNVbEtpWUlzKzRzWElzU1NkUkVJYkdjNEpwWWJVL3lUZklMbzcrQ04vby9wYW8rT1RjQWZDWFhVd2orSmRCYmw3SVk5MW94b2lqeTFEZ0Joa2pWTmNEa24rSlNzZ2FnZHJPcXR2ZCtmdGtnMHcvU3QveVd0Q0J4WDVkdFA4WnBnclg5cEpFOXNQUHRnZXB0M0xmS0RGTU1OK0QyUktwK0RDUEMwWVBGcGt6UHN1d2pDdFJIdEdZRm0yNENkRzFmOUlDQmhPMDN3bmVoWEtEdm5zL2l2eUtVL3RCd1lCeDhtV3JlL05CQ0p6UXRUT0ovbGJlS0tpYWNNdEkyUTNtOWFvVGdNLy8xMFZpQ3Y3eTJGVVB5ZGZXOXFEZ2duYzMrclp4RlNpU3pLK3NFOVNVdFJUNnlJc1VqckFURWpnPT08L2RzOlNpZ25hdHVyZVZhbHVlPgo8ZHM6S2V5SW5mbz48ZHM6WDUwOURhdGE+PGRzOlg1MDlDZXJ0aWZpY2F0ZT5NSUlEVURDQ0FqZ0NDUUROcU9BOTRCOGZhVEFOQmdrcWhraUc5dzBCQVFVRkFEQnFNUXN3Q1FZRFZRUUdFd0pGUlRFUk1BOEdBMVVFQ0JNSVZHRnlkSFZ0WVdFeERqQU1CZ05WQkFjVEJWUmhjblIxTVE0d0RBWURWUVFLRXdWRlJVNWxkREVOTUFzR0ExVUVDeE1FVkVGQlZERVpNQmNHQTFVRUF4TVFjbVZ2Y3k1MFlXRjBMbVZrZFM1bFpUQWVGdzB4TXpBek1EUXhNVFExTlRGYUZ3MHhOakF6TURNeE1UUTFOVEZhTUdveEN6QUpCZ05WQkFZVEFrVkZNUkV3RHdZRFZRUUlFd2hVWVhKMGRXMWhZVEVPTUF3R0ExVUVCeE1GVkdGeWRIVXhEakFNQmdOVkJBb1RCVVZGVG1WME1RMHdDd1lEVlFRTEV3UlVRVUZVTVJrd0Z3WURWUVFERXhCeVpXOXpMblJoWVhRdVpXUjFMbVZsTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUE1VkJ3dk16Yk96VDhveUpUazRQN3A2Z005aE9JZTlQNkcxOHp0Z2VneUpaK1RGYStUYVU4RVhEbmR2QUY1a3VCR0V0SU1nVE51anNLZ3FBeU01Vzd1Wk8rQWE2V0taVTBKSDh6MHVOSEt0eEpUNDlVcDQ0RzYwNDdHa3dwUkgvVlVUL0dVdzJ3elFKaENFZ1BGQWRua2lVRTRlWitna3Nyc2x2UkVQZzRNRE9CQWx2UWQ1aGVqRVlsQm1ESU1MaEtpRExnZFZWelVPVkxCY0ptVitWVk1ubXNJQWJKR2tXcmhwdmtwTlM5NWhsMENwblYranlQNDhWREZTYnVUOFJqdWNKbGJ2ak9kVFVvZEYzUDJ5amZ6YkJIcjE1dURJR0wyNVp3WDd6anJPdWROc3A0VlB6d2xUdW9FdG5HZ3RLK01ldmlzSTl1VmVvYXhKOCtCd3VDSVFJREFRQUJNQTBHQ1NxR1NJYjNEUUVCQlFVQUE0SUJBUUFuNFhnQVlVTGxydzBBb3htN0R0cWlQMnlOY0s0NFdFOTdXZUlmYnE0WFkxTnFNK0U1bUE0cGVwYkZPRzFSRXZJek9HMUcwTVJHUWR4Z2Y4Z1ZLU0FIVGtEdXN1MkdhMnN1dXV3LzYwWDhEb1Q3MnF3OTM0SlhaY0N3M1hLWmdxSy9aSHlXZ21Cd2RNVnVZc0lHWjFkNFpVdkJ5bGRaMWU4MFI3SWxlc3JMWUdWZXY2dmxudStzMDRJYWZqQUp4eThpYzBTTzdDMWxidFByRTdoRTl1dU84NklDTjZvczNWS3NCcmdhczZSN3BCQ3RTTFRpRjA2am1tcXVGSFdvcWowNkhSUkJOdkk3eW1qR3pPYjFLVTJLaEkzelF2S0VwaXRYNWdTTmsyS21PM0NGelFobW15ZHpwbzJjR29GaFBoQlNTQ1JHRTg1bGkyb0YrYVJvUnFUcTwvZHM6WDUwOUNlcnRpZmljYXRlPjwvZHM6WDUwOURhdGE+PC9kczpLZXlJbmZvPjwvZHM6U2lnbmF0dXJlPjxzYW1scDpTdGF0dXM+PHNhbWxwOlN0YXR1c0NvZGUgVmFsdWU9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpzdGF0dXM6U3VjY2VzcyIvPjwvc2FtbHA6U3RhdHVzPjxzYW1sOkFzc2VydGlvbiB4bWxuczp4c2k9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hLWluc3RhbmNlIiB4bWxuczp4cz0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEiIElEPSJfNmM0YjRkZTU0ODM1ZmViODQ2OTkwYmZkOTZlNTU0NDRkNzVkN2ZjNjZiIiBWZXJzaW9uPSIyLjAiIElzc3VlSW5zdGFudD0iMjAxNS0wOS0wNFQxMTo1ODozMVoiPjxzYW1sOklzc3Vlcj5odHRwczovL3Jlb3MudGFhdC5lZHUuZWUvc2FtbDIvaWRwL21ldGFkYXRhLnBocDwvc2FtbDpJc3N1ZXI+PGRzOlNpZ25hdHVyZSB4bWxuczpkcz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+CiAgPGRzOlNpZ25lZEluZm8+PGRzOkNhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz4KICAgIDxkczpTaWduYXR1cmVNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjcnNhLXNoYTEiLz4KICA8ZHM6UmVmZXJlbmNlIFVSST0iI182YzRiNGRlNTQ4MzVmZWI4NDY5OTBiZmQ5NmU1NTQ0NGQ3NWQ3ZmM2NmIiPjxkczpUcmFuc2Zvcm1zPjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjZW52ZWxvcGVkLXNpZ25hdHVyZSIvPjxkczpUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiLz48L2RzOlRyYW5zZm9ybXM+PGRzOkRpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNzaGExIi8+PGRzOkRpZ2VzdFZhbHVlPjUrVm5GRVV3eDdpdTBPcjN2eStpQS9XVkJLTT08L2RzOkRpZ2VzdFZhbHVlPjwvZHM6UmVmZXJlbmNlPjwvZHM6U2lnbmVkSW5mbz48ZHM6U2lnbmF0dXJlVmFsdWU+U2xXbHphTDRRTzRrRk93NDBvUTRFSks1Z2xEMVlBV2xqZ3lPTUp2SmUwOC9HejdScHlpK21kOExtRVVQQjdFMmM5YVpBUEs2MkVsSGZja0hTS2plSi8zWkJjL0xuWmZXMFp6V0NYNVdIWEwza0o0T1NEa25wYlcrSjMxL2JOQVIwSDl3MmxXclBiUUUrOHIyYjk3MlhvRUJpU2ZxSU81d3RoMmZMdTZwZFc4VXhmTGMyZm9ZVTU3bnc0Tmh0cmQ5aEkrVlFxS3grbGdyQjd2QkI2cnoza1hxU1FYNG9QSG5ETDV2MGVRYWV0cFlBSFRoQnZNTHZuSEF3VjNHQm52Y2cwbzM3VmZhdVhuVFpaaU91cFFBRDBuOWg4VFQ0TStxY2w1czFsTHFDREdZZzJjL0lWOXV2VnN0OG9yNUQxSXRRT0JjSjlRRHpma1N0azFneHJydHl3PT08L2RzOlNpZ25hdHVyZVZhbHVlPgo8ZHM6S2V5SW5mbz48ZHM6WDUwOURhdGE+PGRzOlg1MDlDZXJ0aWZpY2F0ZT5NSUlEVURDQ0FqZ0NDUUROcU9BOTRCOGZhVEFOQmdrcWhraUc5dzBCQVFVRkFEQnFNUXN3Q1FZRFZRUUdFd0pGUlRFUk1BOEdBMVVFQ0JNSVZHRnlkSFZ0WVdFeERqQU1CZ05WQkFjVEJWUmhjblIxTVE0d0RBWURWUVFLRXdWRlJVNWxkREVOTUFzR0ExVUVDeE1FVkVGQlZERVpNQmNHQTFVRUF4TVFjbVZ2Y3k1MFlXRjBMbVZrZFM1bFpUQWVGdzB4TXpBek1EUXhNVFExTlRGYUZ3MHhOakF6TURNeE1UUTFOVEZhTUdveEN6QUpCZ05WQkFZVEFrVkZNUkV3RHdZRFZRUUlFd2hVWVhKMGRXMWhZVEVPTUF3R0ExVUVCeE1GVkdGeWRIVXhEakFNQmdOVkJBb1RCVVZGVG1WME1RMHdDd1lEVlFRTEV3UlVRVUZVTVJrd0Z3WURWUVFERXhCeVpXOXpMblJoWVhRdVpXUjFMbVZsTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUE1VkJ3dk16Yk96VDhveUpUazRQN3A2Z005aE9JZTlQNkcxOHp0Z2VneUpaK1RGYStUYVU4RVhEbmR2QUY1a3VCR0V0SU1nVE51anNLZ3FBeU01Vzd1Wk8rQWE2V0taVTBKSDh6MHVOSEt0eEpUNDlVcDQ0RzYwNDdHa3dwUkgvVlVUL0dVdzJ3elFKaENFZ1BGQWRua2lVRTRlWitna3Nyc2x2UkVQZzRNRE9CQWx2UWQ1aGVqRVlsQm1ESU1MaEtpRExnZFZWelVPVkxCY0ptVitWVk1ubXNJQWJKR2tXcmhwdmtwTlM5NWhsMENwblYranlQNDhWREZTYnVUOFJqdWNKbGJ2ak9kVFVvZEYzUDJ5amZ6YkJIcjE1dURJR0wyNVp3WDd6anJPdWROc3A0VlB6d2xUdW9FdG5HZ3RLK01ldmlzSTl1VmVvYXhKOCtCd3VDSVFJREFRQUJNQTBHQ1NxR1NJYjNEUUVCQlFVQUE0SUJBUUFuNFhnQVlVTGxydzBBb3htN0R0cWlQMnlOY0s0NFdFOTdXZUlmYnE0WFkxTnFNK0U1bUE0cGVwYkZPRzFSRXZJek9HMUcwTVJHUWR4Z2Y4Z1ZLU0FIVGtEdXN1MkdhMnN1dXV3LzYwWDhEb1Q3MnF3OTM0SlhaY0N3M1hLWmdxSy9aSHlXZ21Cd2RNVnVZc0lHWjFkNFpVdkJ5bGRaMWU4MFI3SWxlc3JMWUdWZXY2dmxudStzMDRJYWZqQUp4eThpYzBTTzdDMWxidFByRTdoRTl1dU84NklDTjZvczNWS3NCcmdhczZSN3BCQ3RTTFRpRjA2am1tcXVGSFdvcWowNkhSUkJOdkk3eW1qR3pPYjFLVTJLaEkzelF2S0VwaXRYNWdTTmsyS21PM0NGelFobW15ZHpwbzJjR29GaFBoQlNTQ1JHRTg1bGkyb0YrYVJvUnFUcTwvZHM6WDUwOUNlcnRpZmljYXRlPjwvZHM6WDUwOURhdGE+PC9kczpLZXlJbmZvPjwvZHM6U2lnbmF0dXJlPjxzYW1sOlN1YmplY3Q+PHNhbWw6TmFtZUlEIFNQTmFtZVF1YWxpZmllcj0iaHR0cHM6Ly9veHlnZW4ubmV0Z3JvdXBkaWdpdGFsLmNvbS9zcCIgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6bmFtZWlkLWZvcm1hdDp0cmFuc2llbnQiPl81NDdmMzhmYWI5M2IxNDBkZWE1OTk4ODhjNDQ0ZTExZDM2ZTc1Mzc4NzI8L3NhbWw6TmFtZUlEPjxzYW1sOlN1YmplY3RDb25maXJtYXRpb24gTWV0aG9kPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6Y206YmVhcmVyIj48c2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uRGF0YSBOb3RPbk9yQWZ0ZXI9IjIwMTUtMDktMDRUMTI6MDM6MzFaIiBSZWNpcGllbnQ9Imh0dHBzOi8vbG9jYWxob3N0L3Jlc3QvbG9naW4vdGFhdCIgSW5SZXNwb25zZVRvPSJjY3UzMHQ4YWZiMG9idGxsbWsxaW80MGQ5aCIvPjwvc2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uPjwvc2FtbDpTdWJqZWN0PjxzYW1sOkNvbmRpdGlvbnMgTm90QmVmb3JlPSIyMDE1LTA5LTA0VDExOjU4OjAxWiIgTm90T25PckFmdGVyPSIyMDE1LTA5LTA0VDEyOjAzOjMxWiI+PHNhbWw6QXVkaWVuY2VSZXN0cmljdGlvbj48c2FtbDpBdWRpZW5jZT5odHRwczovL294eWdlbi5uZXRncm91cGRpZ2l0YWwuY29tL3NwPC9zYW1sOkF1ZGllbmNlPjwvc2FtbDpBdWRpZW5jZVJlc3RyaWN0aW9uPjwvc2FtbDpDb25kaXRpb25zPjxzYW1sOkF1dGhuU3RhdGVtZW50IEF1dGhuSW5zdGFudD0iMjAxNS0wOS0wNFQxMTo1ODozMVoiIFNlc3Npb25Ob3RPbk9yQWZ0ZXI9IjIwMTUtMDktMDRUMTk6NTg6MzFaIiBTZXNzaW9uSW5kZXg9Il8wN2NiNzhmODI2OTRmYjdlNWQ4YjU3YTc3ZDZiZWJiYWVjYjU3NDg1NGUiPjxzYW1sOkF1dGhuQ29udGV4dD48c2FtbDpBdXRobkNvbnRleHRDbGFzc1JlZj51cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YWM6Y2xhc3NlczpQYXNzd29yZDwvc2FtbDpBdXRobkNvbnRleHRDbGFzc1JlZj48c2FtbDpBdXRoZW50aWNhdGluZ0F1dGhvcml0eT5odHRwczovL2VpdGphLnRhYXQuZWR1LmVlL3Rlc3QtaWRwL3NhbWwyL2lkcC9tZXRhZGF0YS5waHA8L3NhbWw6QXV0aGVudGljYXRpbmdBdXRob3JpdHk+PC9zYW1sOkF1dGhuQ29udGV4dD48L3NhbWw6QXV0aG5TdGF0ZW1lbnQ+PHNhbWw6QXR0cmlidXRlU3RhdGVtZW50PjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJzY2hhY1BlcnNvbmFsVW5pcXVlSUQiIE5hbWVGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphdHRybmFtZS1mb3JtYXQ6dXJpIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZSB4c2k6dHlwZT0ieHM6c3RyaW5nIj5lZTpFSUQ6MTExMTExMTExMTE8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0ic2NoYWNIb21lT3JnYW5pemF0aW9uIiBOYW1lRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXR0cm5hbWUtZm9ybWF0OnVyaSI+PHNhbWw6QXR0cmlidXRlVmFsdWUgeHNpOnR5cGU9InhzOnN0cmluZyI+ZWVuZXQuZWU8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0idXJuOm1hY2U6ZGlyOmF0dHJpYnV0ZS1kZWY6c24iIE5hbWVGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphdHRybmFtZS1mb3JtYXQ6dXJpIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZSB4c2k6dHlwZT0ieHM6c3RyaW5nIj5UZXN0UGVyZW5pbWk8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0idXJuOm1hY2U6ZGlyOmF0dHJpYnV0ZS1kZWY6Y24iIE5hbWVGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphdHRybmFtZS1mb3JtYXQ6dXJpIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZSB4c2k6dHlwZT0ieHM6c3RyaW5nIj5UZXN0VMOkaXNuaW1pPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9InVybjptYWNlOmRpcjphdHRyaWJ1dGUtZGVmOm1haWwiIE5hbWVGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphdHRybmFtZS1mb3JtYXQ6dXJpIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZSB4c2k6dHlwZT0ieHM6c3RyaW5nIj50ZXN0QHRlc3RtYWlsLmVlPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9InVybjptYWNlOmRpcjphdHRyaWJ1dGUtZGVmOnByZWZlcnJlZExhbmd1YWdlIiBOYW1lRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXR0cm5hbWUtZm9ybWF0OnVyaSI+PHNhbWw6QXR0cmlidXRlVmFsdWUgeHNpOnR5cGU9InhzOnN0cmluZyI+ZXQ8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0idXJuOm1hY2U6ZGlyOmF0dHJpYnV0ZS1kZWY6ZWR1UGVyc29uQWZmaWxpYXRpb24iIE5hbWVGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphdHRybmFtZS1mb3JtYXQ6dXJpIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZSB4c2k6dHlwZT0ieHM6c3RyaW5nIj5tZW1iZXI8L3NhbWw6QXR0cmlidXRlVmFsdWU+PHNhbWw6QXR0cmlidXRlVmFsdWUgeHNpOnR5cGU9InhzOnN0cmluZyI+c3R1ZGVudDwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48L3NhbWw6QXR0cmlidXRlPjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJ1cm46bWFjZTpkaXI6YXR0cmlidXRlLWRlZjplZHVQZXJzb25TY29wZWRBZmZpbGlhdGlvbiIgTmFtZUZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmF0dHJuYW1lLWZvcm1hdDp1cmkiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIHhzaTp0eXBlPSJ4czpzdHJpbmciPnN0dWRlbnRAYmFrLnN0dWR5bGV2ZWwudGFhdC5lZHUuZWU8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0idXJuOm1hY2U6ZGlyOmF0dHJpYnV0ZS1kZWY6ZWR1UGVyc29uVGFyZ2V0ZWRJRCIgTmFtZUZvcm1hdD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmF0dHJuYW1lLWZvcm1hdDp1cmkiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIHhzaTp0eXBlPSJ4czpzdHJpbmciPiQyJGJmNTUwZmIyZjlmZTZkZDI4NGI4NDc5ZjU3YzdkNjBhY2YxMzhmNTk0OGYwMjQ5NTBmNjRhNTc5NDQ4ZWVhMWY8L3NhbWw6QXR0cmlidXRlVmFsdWU+PHNhbWw6QXR0cmlidXRlVmFsdWUgeHNpOnR5cGU9InhzOnN0cmluZyI+JDEkNTRlMzk2JGExNmY3MzBjY2E1MDQ4NWYwMTliYTBkOGUzMjBhZjlmZjczMzE2ZDEwOTAwNGY4ZGFhNDllNmY0YTQxYTQwYjI8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48L3NhbWw6QXR0cmlidXRlU3RhdGVtZW50Pjwvc2FtbDpBc3NlcnRpb24+PC9zYW1scDpSZXNwb25zZT4=";
    }

    private String encodeQuery(String query) {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return encodedQuery;
    }
}
