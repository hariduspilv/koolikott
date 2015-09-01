package ee.hm.dop.service;

import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_FILENAME;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_ID;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_ASSERTION_CONSUMER_SERVICE_INDEX;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_CONNECTION_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_SSO;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;

@RunWith(EasyMockRunner.class)
public class TaatServiceTest {

    @TestSubject
    private TaatService taatService = new TaatService();

    @Mock
    private Configuration configuration;

    @Test
    public void buildAuthnRequest() {
        Integer assertionConsumerServiceIndex = 3;
        String connectionId = "https://app.website/sp";

        expect(configuration.getString(TAAT_ASSERTION_CONSUMER_SERVICE_INDEX))
                .andReturn(String.valueOf(assertionConsumerServiceIndex));
        expect(configuration.getString(TAAT_CONNECTION_ID)).andReturn(connectionId);

        replay(configuration);

        AuthnRequest authnRequest = taatService.buildAuthnRequest();

        verify(configuration);

        assertNotNull(authnRequest);
        assertNotNull(authnRequest.getID());
        Interval interval = new Interval(authnRequest.getIssueInstant(), new DateTime());
        assertTrue(authnRequest.getIssueInstant().isBeforeNow());
        assertTrue(interval.toDurationMillis() < 1000);
        assertEquals(assertionConsumerServiceIndex, authnRequest.getAssertionConsumerServiceIndex());
        assertNotNull(authnRequest.getIssuer());
        assertEquals(connectionId, authnRequest.getIssuer().getValue());
        assertEquals(SAMLVersion.VERSION_20, authnRequest.getVersion());
        NameIDPolicy nameIdPolicy = authnRequest.getNameIDPolicy();
        assertNotNull(nameIdPolicy);
        assertEquals("urn:oasis:names:tc:SAML:2.0:nameid-format:transient", nameIdPolicy.getFormat());
        assertTrue(nameIdPolicy.getAllowCreate());
    }

    @Test
    public void buildMessageContext() {

        AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();
        AuthnRequest authnRequest = authRequestBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol",
                "AuthnRequest", "samlp");

        HttpServletResponse response = createMock(HttpServletResponse.class);

        String endpointLocation = "https://test.taat.ee/notrealurl";

        expect(configuration.getString(KEYSTORE_FILENAME)).andReturn("test.keystore");
        expect(configuration.getString(KEYSTORE_PASSWORD)).andReturn("newKeyStorePass");
        expect(configuration.getString(TAAT_SSO)).andReturn("https://test.taat.ee/notrealurl");
        expect(configuration.getString(KEYSTORE_SIGNING_ENTITY_ID)).andReturn("testAlias");
        expect(configuration.getString(KEYSTORE_SIGNING_ENTITY_PASSWORD)).andReturn("newKeyPass");

        replay(configuration);

        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = taatService
                .buildMessageContext(authnRequest, response);

        verify(configuration);

        assertEquals(endpointLocation, context.getPeerEntityEndpoint().getLocation());
        assertEquals(authnRequest, context.getOutboundSAMLMessage());
        assertNotNull(context.getOutboundSAMLMessage());
        assertNotNull(context.getRelayState());
    }

    @Test
    public void getIssuer() {
        String connectionId = "test.taat.edu";

        Issuer issuer = taatService.getIssuer(connectionId);

        assertEquals(connectionId, issuer.getValue());
    }

}
