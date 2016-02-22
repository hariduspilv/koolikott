package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.TAAT_ASSERTION_CONSUMER_SERVICE_INDEX;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_CONNECTION_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_SSO;
import static org.opensaml.xml.Configuration.getUnmarshallerFactory;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.xml.security.utils.Base64;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.impl.EndpointImpl;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.impl.XSStringImpl;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.security.KeyStoreUtils;

public class TaatService {

    private static final String NAME = "urn:mace:dir:attribute-def:cn";

    private static final String SURNAME = "urn:mace:dir:attribute-def:sn";

    private static final String ID_CODE = "schacPersonalUniqueID";

    @Inject
    private Configuration configuration;

    @Inject
    private AuthenticationStateDAO authenticationStateDAO;

    @Inject
    private LoginService loginService;

    @Inject
    private SignatureValidator validator;

    private static final SecureRandom random = new SecureRandom();

    private AuthnRequest buildAuthnRequest() {
        int assertionConsumerServiceIndex = Integer
                .valueOf(configuration.getString(TAAT_ASSERTION_CONSUMER_SERVICE_INDEX));

        Issuer issuer = getIssuer(configuration.getString(TAAT_CONNECTION_ID));
        NameIDPolicy nameIdPolicy = getNameIdPolicy();

        AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();
        AuthnRequest authnRequest = authRequestBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol",
                "AuthnRequest", "samlp");
        authnRequest.setID(new BigInteger(130, random).toString(32));
        authnRequest.setIssueInstant(new DateTime());
        authnRequest.setAssertionConsumerServiceIndex(assertionConsumerServiceIndex);
        authnRequest.setIssuer(issuer);
        authnRequest.setVersion(SAMLVersion.VERSION_20);
        authnRequest.setNameIDPolicy(nameIdPolicy);

        return authnRequest;
    }

    public BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> buildMessageContext(
            HttpServletResponse response) {
        String token = new BigInteger(130, random).toString(32);
        createAuthenticationState(token);

        HttpServletResponseAdapter responseAdapter = new HttpServletResponseAdapter(response, true);
        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = new BasicSAMLMessageContext<>();
        context.setPeerEntityEndpoint(getEndpoint());
        context.setOutboundSAMLMessage(buildAuthnRequest());
        context.setOutboundSAMLMessageSigningCredential(KeyStoreUtils.getDOPSigningCredential(configuration));
        context.setOutboundMessageTransport(responseAdapter);
        context.setRelayState(token);

        return context;
    }

    public AuthenticatedUser authenticate(String responseMessage, String authenticationStateToken) {
        validateAuthenticationToken(authenticationStateToken);

        Response response = getResponse(responseMessage);
        validateResponseSignature(response);

        Map<String, String> dataMap = parseAttributes(response);
        return login(dataMap);
    }

    private AuthenticatedUser login(Map<String, String> dataMap) {
        return loginService.logIn(dataMap.get(ID_CODE), dataMap.get(NAME), dataMap.get(SURNAME));
    }

    private void validateAuthenticationToken(String authenticationStateToken) {
        AuthenticationState authenticationState = authenticationStateDAO
                .findAuthenticationStateByToken(authenticationStateToken);
        if (authenticationState == null) {
            throw new RuntimeException("Error validating authentication state.");
        } else {
            authenticationStateDAO.delete(authenticationState);
        }
    }

    private void validateResponseSignature(Response response) {
        Signature sig = response.getSignature();

        try {
            validator.validate(sig);
        } catch (ValidationException e) {
            throw new RuntimeException("Error validating signature", e);
        }
    }

    private Issuer getIssuer(String connectionId) {
        IssuerBuilder issuerBuilder = new IssuerBuilder();
        Issuer issuer = issuerBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "Issuer", "saml");
        issuer.setValue(connectionId);
        return issuer;
    }

    private NameIDPolicy getNameIdPolicy() {
        NameIDPolicyBuilder nameIdPolicyBuilder = new NameIDPolicyBuilder();
        NameIDPolicy nameIdPolicy = nameIdPolicyBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol",
                "NameIDPolicy", "samlp");
        nameIdPolicy.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
        nameIdPolicy.setAllowCreate(true);
        return nameIdPolicy;
    }

    private Endpoint getEndpoint() {
        Endpoint endpoint = new EndpointImpl("urn:oasis:names:tc:SAML:2.0:metadata", "Endpoint", "md") {
        };
        endpoint.setLocation(configuration.getString(TAAT_SSO));
        return endpoint;
    }

    private AuthenticationState createAuthenticationState(String token) {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setCreated(new DateTime());
        authenticationState.setToken(token);
        return authenticationStateDAO.createAuthenticationState(authenticationState);
    }

    private Map<String, String> parseAttributes(Response response) {
        Assertion assertion = response.getAssertions().get(0);
        List<Attribute> attributes = assertion.getAttributeStatements().get(0).getAttributes();

        Map<String, String> attributeMap = new HashMap<>();

        for (Attribute attribute : attributes) {
            for (XMLObject xmlObject : attribute.getAttributeValues()) {
                String value = ((XSStringImpl) xmlObject).getValue();

                switch (attribute.getName()) {
                    case ID_CODE:
                        String[] parts = value.split(":");
                        attributeMap.put(ID_CODE, parts[parts.length - 1]);
                        break;
                    case SURNAME:
                        attributeMap.put(SURNAME, value);
                        break;
                    case NAME:
                        attributeMap.put(NAME, value);
                        break;
                    default:
                        break;
                }
            }
        }

        return attributeMap;
    }

    private Response getResponse(String responseMessage) {
        try {
            byte[] base64DecodedResponse;
            base64DecodedResponse = Base64.decode(responseMessage);
            Element element = buildXmlElementFrom(base64DecodedResponse);
            return (Response) unmarshall(element);
        } catch (Exception e) {
            throw new RuntimeException("Error processing data received from Taat.", e);
        }
    }

    private XMLObject unmarshall(Element element) throws UnmarshallingException {
        UnmarshallerFactory unmarshallerFactory = getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        return unmarshaller.unmarshall(element);
    }

    private Element buildXmlElementFrom(byte[] xml) throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(xml);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = docBuilder.parse(is);
        return document.getDocumentElement();
    }

}
