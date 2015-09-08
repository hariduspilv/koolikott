package ee.hm.dop.service;

import static ee.hm.dop.guice.GuiceInjector.getInjector;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_FILENAME;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_ID;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_ASSERTION_CONSUMER_SERVICE_INDEX;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_CONNECTION_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_SSO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.List;
import java.util.StringJoiner;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.Configuration;
import org.apache.xml.security.exceptions.Base64DecodingException;
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
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.inject.Singleton;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.User;
import ee.hm.dop.security.KeyStoreUtils;
import ee.hm.dop.security.MetadataUtils;

@Singleton
public class TaatService {

    private static final Logger logger = LoggerFactory.getLogger(TaatService.class);

    @Inject
    private Configuration configuration;

    private SecureRandom random;

    private KeyStore keyStore;

    private Credential credential;

    private AuthenticatedUser authenticatedUser;

    public TaatService() {
        random = new SecureRandom();
        credential = getCerdential();
    }

    private KeyStore getKeyStore() {
        if (keyStore == null) {
            String filename = configuration.getString(KEYSTORE_FILENAME);
            String password = configuration.getString(KEYSTORE_PASSWORD);
            keyStore = KeyStoreUtils.loadKeystore(filename, password);
        }

        return keyStore;
    }

    public AuthnRequest buildAuthnRequest() {
        int assertionConsumerServiceIndex = Integer
                .valueOf(configuration.getString(TAAT_ASSERTION_CONSUMER_SERVICE_INDEX));

        Issuer issuer = getIssuer(configuration.getString(TAAT_CONNECTION_ID));
        NameIDPolicy nameIdPolicy = getNameIdPolicy();

        AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();
        AuthnRequest authnRequest = authRequestBuilder
                .buildObject("urn:oasis:names:tc:SAML:2.0:protocol", "AuthnRequest", "samlp");
        authnRequest.setID(new BigInteger(130, random).toString(32));
        authnRequest.setIssueInstant(new DateTime());
        authnRequest.setAssertionConsumerServiceIndex(assertionConsumerServiceIndex);
        authnRequest.setIssuer(issuer);
        authnRequest.setVersion(SAMLVersion.VERSION_20);
        authnRequest.setNameIDPolicy(nameIdPolicy);

        return authnRequest;
    }

    public BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> buildMessageContext(AuthnRequest authnRequest,
            HttpServletResponse response) {
        String token = new BigInteger(130, random).toString(32);
        createAuthenticationState(token);

        HttpServletResponseAdapter responseAdapter = new HttpServletResponseAdapter(response, true);
        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = new BasicSAMLMessageContext<>();
        context.setPeerEntityEndpoint(getEndpoint());
        context.setOutboundSAMLMessage(authnRequest);
        context.setOutboundSAMLMessageSigningCredential(getSigningCredential());
        context.setOutboundMessageTransport(responseAdapter);
        context.setRelayState(token);

        return context;
    }

    protected Issuer getIssuer(String connectionId) {
        IssuerBuilder issuerBuilder = new IssuerBuilder();
        Issuer issuer = issuerBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "Issuer", "saml");
        issuer.setValue(connectionId);
        return issuer;
    }

    protected NameIDPolicy getNameIdPolicy() {
        NameIDPolicyBuilder nameIdPolicyBuilder = new NameIDPolicyBuilder();
        NameIDPolicy nameIdPolicy = nameIdPolicyBuilder
                .buildObject("urn:oasis:names:tc:SAML:2.0:protocol", "NameIDPolicy", "samlp");
        nameIdPolicy.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
        nameIdPolicy.setAllowCreate(true);
        return nameIdPolicy;
    }

    protected Endpoint getEndpoint() {
        Endpoint endpoint = new EndpointImpl("urn:oasis:names:tc:SAML:2.0:metadata", "Endpoint", "md") {
        };
        endpoint.setLocation(configuration.getString(TAAT_SSO));
        return endpoint;
    }

    protected Credential getSigningCredential() {
        String entityId = configuration.getString(KEYSTORE_SIGNING_ENTITY_ID);
        String entityPassword = configuration.getString(KEYSTORE_SIGNING_ENTITY_PASSWORD);
        return KeyStoreUtils.getSigningCredential(getKeyStore(), entityId, entityPassword);
    }

    protected AuthenticationState createAuthenticationState(String token) {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setToken(token);
        AuthenticationStateService authenticationStateService = newAuthenticationStateService();
        return authenticationStateService.create(authenticationState);
    }

    protected AuthenticationStateService newAuthenticationStateService() {
        return getInjector().getInstance(AuthenticationStateService.class);
    }

    public AuthenticatedUser authenticate(String responseMessage) {
        LoginService loginService = getInjector().getInstance(LoginService.class);
        Response response;

        try {
            response = getResponse(responseMessage);
        } catch (Exception e) {
            throw  new RuntimeException("Error processing data received from Taat.", e);
        }

        Signature sig = response.getSignature();

        try {
            SignatureValidator validator = new SignatureValidator(credential);
            validator.validate(sig);
        } catch (ValidationException e) {
            throw  new RuntimeException("Error validating signature", e);
        }

        List<Attribute> attributes = getAttributes(response);

        authenticatedUser = getUserData(attributes);
        User user = createUser(authenticatedUser.getUser());
        authenticatedUser.setUser(user);

        return loginService.createAuthenticatedUser(authenticatedUser);
    }

    private List<Attribute> getAttributes(Response response) {
        Assertion assertion = response.getAssertions().get(0);
        return assertion.getAttributeStatements().get(0).getAttributes();
    }

    private User createUser(User user) {
        UserService userService = getInjector().getInstance(UserService.class);

        User oldUser = userService.getUserByIdCode(user.getIdCode());
        if (oldUser == null) {
            authenticatedUser.setFirstLogin(true);
            user = userService.create(user);
        } else {
            user = oldUser;
        }
        return user;
    }

    private AuthenticatedUser getUserData(List<Attribute> attributes) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        User user = new User();
        StringJoiner mails = new StringJoiner(",");
        StringJoiner affiliations = new StringJoiner(",");
        StringJoiner scopedAffiliations = new StringJoiner(",");

        for (Attribute attribute : attributes) {
            for (XMLObject xmlObject : attribute.getAttributeValues()) {
                String value = ((XSStringImpl) xmlObject).getValue();

                switch (attribute.getName()) {
                case "schacPersonalUniqueID":
                    String[] parts = value.split(":");
                    user.setIdCode(parts[parts.length - 1]);
                    break;
                case "urn:mace:dir:attribute-def:sn":
                    user.setSurname(value);
                    break;
                case "urn:mace:dir:attribute-def:cn":
                    user.setName(value);
                    break;
                case "schacHomeOrganization":
                    authenticatedUser.setHomeOrganization(value);
                    break;
                case "urn:mace:dir:attribute-def:mail":
                    mails.add(value);
                    break;
                case "urn:mace:dir:attribute-def:eduPersonAffiliation":
                    affiliations.add(value);
                    break;
                case "urn:mace:dir:attribute-def:eduPersonScopedAffiliation":
                    scopedAffiliations.add(value);
                    break;
                default:
                    break;
                }
            }
        }
        authenticatedUser.setMails(mails.toString());
        authenticatedUser.setAffiliations(affiliations.toString());
        authenticatedUser.setScopedAffiliations(scopedAffiliations.toString());
        authenticatedUser.setUser(user);
        return authenticatedUser;
    }

    private Response getResponse(String responseMessage)
            throws Base64DecodingException, ParserConfigurationException, SAXException, IOException,
            UnmarshallingException {
        Response response;
        byte[] base64DecodedResponse = Base64.decode(responseMessage);

        ByteArrayInputStream is = new ByteArrayInputStream(base64DecodedResponse);

        DocumentBuilder docBuilder = getDocumentBuilder();

        Element element = getElement(is, docBuilder);

        response = (Response) getXmlObject(element);
        return response;
    }

    private XMLObject getXmlObject(Element element) throws UnmarshallingException {
        UnmarshallerFactory unmarshallerFactory = org.opensaml.Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
        return unmarshaller.unmarshall(element);
    }

    private Element getElement(ByteArrayInputStream is, DocumentBuilder docBuilder) throws SAXException, IOException {
        Document document = docBuilder.parse(is);
        return document.getDocumentElement();
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        return documentBuilderFactory.newDocumentBuilder();
    }

    private X509Credential getCerdential() {
        try {
            return MetadataUtils.getCredential("reos_metadata.xml");
        } catch (Exception e) {
            throw new RuntimeException("Error getting credential.", e);
        }
    }
}
