package ee.hm.dop.service;

import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.User;
import ee.hm.dop.security.KeyStoreUtils;
import ee.hm.dop.security.MetadataUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.xml.security.utils.Base64;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.core.*;
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
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static org.opensaml.xml.Configuration.getUnmarshallerFactory;

public class TaatService {

    private static final String SCOPED_AFFILIATION = "urn:mace:dir:attribute-def:eduPersonScopedAffiliation";

    private static final String AFFILIATION = "urn:mace:dir:attribute-def:eduPersonAffiliation";

    private static final String MAIL = "urn:mace:dir:attribute-def:mail";

    private static final String HOME_ORGANIZATION = "schacHomeOrganization";

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
    private UserService userService;

    private static final SecureRandom random = new SecureRandom();

    private static Credential credential;

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

        AuthenticatedUser authenticatedUser = getAuthenticatedUser(dataMap);

        User user = userService.getUserByIdCode(dataMap.get(ID_CODE));
        if (user == null) {
            user = userService.create(dataMap.get(ID_CODE), dataMap.get(NAME), dataMap.get(SURNAME));
            authenticatedUser.setFirstLogin(true);
        }

        authenticatedUser.setUser(user);

        return loginService.createAuthenticatedUser(authenticatedUser);
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
            SignatureValidator validator = new SignatureValidator(getCredential());
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

    private AuthenticatedUser getAuthenticatedUser(Map<String, String> dataMap) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setMails(dataMap.get(MAIL));
        authenticatedUser.setAffiliations(dataMap.get(AFFILIATION));
        authenticatedUser.setScopedAffiliations(dataMap.get(SCOPED_AFFILIATION));
        authenticatedUser.setHomeOrganization(dataMap.get(HOME_ORGANIZATION));
        return authenticatedUser;
    }

    private Map<String, String> parseAttributes(Response response) {
        Assertion assertion = response.getAssertions().get(0);
        List<Attribute> attributes = assertion.getAttributeStatements().get(0).getAttributes();

        StringJoiner mails = new StringJoiner(",");
        StringJoiner affiliations = new StringJoiner(",");
        StringJoiner scopedAffiliations = new StringJoiner(",");

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
                    case HOME_ORGANIZATION:
                        attributeMap.put(HOME_ORGANIZATION, value);
                        break;
                    case MAIL:
                        mails.add(value);
                        break;
                    case AFFILIATION:
                        affiliations.add(value);
                        break;
                    case SCOPED_AFFILIATION:
                        scopedAffiliations.add(value);
                        break;
                    default:
                        break;
                }

                attributeMap.put(MAIL, mails.toString());
                attributeMap.put(AFFILIATION, affiliations.toString());
                attributeMap.put(SCOPED_AFFILIATION, scopedAffiliations.toString());
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

    private X509Credential getCredential() {
        if (credential == null) {
            try {
                return MetadataUtils.getCredential(configuration.getString(TAAT_METADATA_FILEPATH),
                        configuration.getString(TAAT_METADATA_ENTITY_ID));
            } catch (Exception e) {
                throw new RuntimeException("Error getting credential.", e);
            }
        }

        return (X509Credential) credential;
    }
}
