package ee.hm.dop.rest;

import static ee.hm.dop.utils.ConfigurationProperties.TAAT_ASSERTION_CONSUMER_SERVICE_INDEX;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_CONNECTION_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_SSO;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.util.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.service.AuthenticationStateService;

public class LoginResourceTest extends ResourceIntegrationTestBase {

    @Inject
    private Configuration configuration;

    @Inject
    private AuthenticationStateService authenticationStateService;

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
        assertTrue(interval.toDurationMillis() < 1000);

        assertEquals(Integer.valueOf(configuration.getString(TAAT_ASSERTION_CONSUMER_SERVICE_INDEX)),
                authnRequest.getAssertionConsumerServiceIndex());

        assertNotNull(signature);
        assertNotNull(signatureAlgorithm);

        AuthenticationState authenticationState = authenticationStateService.getAuthenticationStateByToken(token);
        assertNotNull(authenticationState);

        authenticationStateService.delete(authenticationState);
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
}
