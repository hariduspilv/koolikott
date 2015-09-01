package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_FILENAME;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_ID;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_ASSERTION_CONSUMER_SERVICE_INDEX;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_CONNECTION_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_SSO;

import java.math.BigInteger;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.impl.EndpointImpl;
import org.opensaml.ws.transport.http.HttpServletResponseAdapter;
import org.opensaml.xml.security.credential.Credential;

import com.google.inject.Singleton;

import ee.hm.dop.security.KeyStoreUtils;

@Singleton
public class TaatService {

    @Inject
    private Configuration configuration;

    private SecureRandom random;

    private KeyStore keyStore;

    public TaatService() {
        random = new SecureRandom();
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

    public BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> buildMessageContext(AuthnRequest authnRequest,
            HttpServletResponse response) {
        HttpServletResponseAdapter responseAdapter = new HttpServletResponseAdapter(response, true);
        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = new BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject>();
        context.setPeerEntityEndpoint(getEndpoint());
        context.setOutboundSAMLMessage(authnRequest);
        context.setOutboundSAMLMessageSigningCredential(getSigningCredential());
        context.setOutboundMessageTransport(responseAdapter);
        context.setRelayState(new BigInteger(130, random).toString(32));
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
        NameIDPolicy nameIdPolicy = nameIdPolicyBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol",
                "NameIDPolicy", "samlp");
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

}
