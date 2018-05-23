package ee.hm.dop.rest.login;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.model.AuthenticationState;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import static ee.hm.dop.rest.login.LoginTestUtil.SAML_Response;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_ASSERTION_CONSUMER_SERVICE_INDEX;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_CONNECTION_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_SSO;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class TaatLoginResourceTest extends ResourceIntegrationTestBase {

    @Inject
    private AuthenticationStateDao authenticationStateDao;

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
        redirectDefault(response);
    }

    @Test
    public void authenticateWrongData() {
        MultivaluedMap<String, String> formParams = new MultivaluedStringMap();
        formParams.add("SAMLResponse", "wrongResponse");

        Response response = doPost("login/taat", Entity.entity(formParams, MediaType.WILDCARD_TYPE));
        redirectDefault(response);
    }

    @Test
    public void authenticateNoRelayState() {
        MultivaluedMap<String, String> formParams = new MultivaluedStringMap();
        formParams.add("SAMLResponse", SAML_Response);

        Response response = doPost("login/taat", Entity.entity(formParams, MediaType.WILDCARD_TYPE));
        redirectDefault(response);
    }

    @Test
    public void authenticateWrongRelayStateToken() {
        MultivaluedMap<String, String> formParams = new MultivaluedStringMap();
        formParams.add("SAMLResponse", SAML_Response);
        formParams.add("RelayState", "WRONGTOKEN");

        Response response = doPost("login/taat", Entity.entity(formParams, MediaType.WILDCARD_TYPE));
        redirectDefault(response);
    }

    @Test
    public void taatLogin_returns_found_status() throws Exception {
        Response response = doGet("login/taat");
        assertEquals(Response.Status.FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void taatAuthenticate() {
        MultivaluedMap<String, String> formParams = new MultivaluedStringMap();
        formParams.add("SAMLResponse", SAML_Response);
        formParams.add("RelayState", "taatAuthenticateTestToken");

        Response response = doPost("login/taat", Entity.entity(formParams, MediaType.WILDCARD_TYPE));
        String url = response.getHeaderString("Location");
        String[] tokens = url.split("=");
        tokens = tokens[0].split("\\/");
        assertEquals(307, response.getStatus());
        assertEquals("loginRedirect?token", tokens[4]);
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

    private void redirectDefault(Response response) {
        assertEquals(307, response.getStatus());
        assertTrue(response.getHeaderString("Location").endsWith("#!/loginRedirect"));
    }
}
