package ee.hm.dop.service.login;

import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.enums.LoginFrom;
import ee.hm.dop.rest.login.LoginTestUtil;
import ee.hm.dop.service.login.dto.UserStatus;
import ee.hm.dop.utils.security.KeyStoreUtils;
import org.apache.commons.configuration2.Configuration;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import java.time.LocalDateTime;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;

import javax.servlet.http.HttpServletResponse;

import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_FILENAME;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_ID;
import static ee.hm.dop.utils.ConfigurationProperties.KEYSTORE_SIGNING_ENTITY_PASSWORD;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_ASSERTION_CONSUMER_SERVICE_INDEX;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_CONNECTION_ID;
import static ee.hm.dop.utils.ConfigurationProperties.TAAT_SSO;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(EasyMockRunner.class)
public class TaatServiceTest {

    @TestSubject
    private TaatService taatService = new TaatService();
    @Mock
    private Configuration configuration;
    @Mock
    private AuthenticationStateDao authenticationStateDao;
    @Mock
    private LoginService loginService;
    @Mock
    private SignatureValidator validator;

    @BeforeClass
    public static void init() throws ConfigurationException {
        DefaultBootstrap.bootstrap();
        KeyStoreUtils.setKeyStore(null);
    }

    @Test
    public void buildMessageContext() {
        Integer assertionConsumerServiceIndex = 3;
        String connectionId = "https://app.website/sp";

        expect(configuration.getString(TAAT_ASSERTION_CONSUMER_SERVICE_INDEX))
                .andReturn(String.valueOf(assertionConsumerServiceIndex));
        expect(configuration.getString(TAAT_CONNECTION_ID)).andReturn(connectionId);

        HttpServletResponse response = createMock(HttpServletResponse.class);
        AuthenticationState authenticationState = createMock(AuthenticationState.class);

        String endpointLocation = "https://test.taat.ee/notrealurl";

        expect(configuration.getString(KEYSTORE_FILENAME)).andReturn("test.keystore");
        expect(configuration.getString(KEYSTORE_PASSWORD)).andReturn("newKeyStorePass");
        expect(configuration.getString(TAAT_SSO)).andReturn("https://test.taat.ee/notrealurl");
        expect(configuration.getString(KEYSTORE_SIGNING_ENTITY_ID)).andReturn("testAlias");
        expect(configuration.getString(KEYSTORE_SIGNING_ENTITY_PASSWORD)).andReturn("newKeyPass");
        expect(authenticationStateDao.createAuthenticationState(anyObject(AuthenticationState.class)))
                .andReturn(authenticationState);

        replayAll();
        BasicSAMLMessageContext<SAMLObject, AuthnRequest, SAMLObject> context = taatService
                .buildMessageContext(response);

        verifyAll();
        assertEquals(endpointLocation, context.getPeerEntityEndpoint().getLocation());
        assertNotNull(context.getRelayState());

        // Verify authnRequest
        AuthnRequest authnRequest = context.getOutboundSAMLMessage();
        assertNotNull(authnRequest);
        assertNotNull(authnRequest.getID());
        assertEquals(connectionId, authnRequest.getIssuer().getValue());
        assertEquals(assertionConsumerServiceIndex, authnRequest.getAssertionConsumerServiceIndex());
        assertEquals(SAMLVersion.VERSION_20, authnRequest.getVersion());

        DateTime issueInstant = authnRequest.getIssueInstant();
        Interval interval = new Interval(issueInstant, new DateTime());
        assertFalse(issueInstant.isAfterNow());
        assertTrue(interval.toDurationMillis() < 1000);

        NameIDPolicy nameIdPolicy = authnRequest.getNameIDPolicy();
        assertNotNull(nameIdPolicy);
        assertEquals("urn:oasis:names:tc:SAML:2.0:nameid-format:transient", nameIdPolicy.getFormat());
        assertTrue(nameIdPolicy.getAllowCreate());
    }

    @Test
    public void authenticateNullAuthenticationState() throws Exception {
        String authenticationStateToken = "token";
        expect(authenticationStateDao.findAuthenticationStateByToken(authenticationStateToken)).andReturn(null);

        replayAll();
        try {
            taatService.authenticate(getSAMLResponse(), authenticationStateToken);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals("Error validating authentication state.", e.getMessage());
        }

        verifyAll();
    }

    @Test
    public void authenticateResponseException() throws Exception {
        String authenticationStateToken = setAuthenticationExpects();

        replayAll();

        try {
            taatService.authenticate("djiesoifjdeoaishfdshfudshfuasdhf", authenticationStateToken);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals("Error processing data received from Taat.", e.getMessage());
        }

        verifyAll();
    }

    @Test
    public void authenticateErrorValidatingSignature() throws Exception {
        String authenticationStateToken = setAuthenticationExpects();

        validator.validate(EasyMock.anyObject(Signature.class));
        EasyMock.expectLastCall().andThrow(new ValidationException());

        replayAll();

        try {
            taatService.authenticate(getSAMLResponse(), authenticationStateToken);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals("Error validating signature", e.getMessage());
        }

        verifyAll();
    }

    @Test
    public void authenticate() throws Exception {
        String authenticationStateToken = setAuthenticationExpects();

        String idCode = "11111111111";
        AuthenticatedUser authenticatedUser = createMock(AuthenticatedUser.class);
        UserStatus userStatus = UserStatus.loggedIn(authenticatedUser);
        expect(loginService.login(idCode, "TestTäisnimi", "TestPerenimi", LoginFrom.TAAT)).andReturn(userStatus);

        validator.validate(EasyMock.anyObject(Signature.class));

        replayAll(authenticatedUser);

        UserStatus returnedAuthenticatedUser = taatService.authenticate(getSAMLResponse(),
                authenticationStateToken);

        verifyAll(authenticatedUser);

        assertSame(userStatus, returnedAuthenticatedUser);

    }

    /**
     * Returns a deflated and base64 encoded SAMLResponse. In
     * TaatService.getResponse(), this gets parsed into an XMLObject and casted
     * into a Response. The signature in the response does not really matter as
     * we have a mock of the SignatureValidator.
     * 
     * @return SAMLResponse
     */
    private String getSAMLResponse() {
        return LoginTestUtil.SAML_Response;
    }

    private String setAuthenticationExpects() {
        String authenticationStateToken = "token";
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setId(1452L);
        authenticationState.setToken(authenticationStateToken);

        expect(authenticationStateDao.findAuthenticationStateByToken(authenticationStateToken))
                .andReturn(authenticationState);
        authenticationStateDao.delete(authenticationState);

        return authenticationStateToken;
    }

    private void replayAll(Object... mocks) {
        replay(configuration, loginService, authenticationStateDao, validator);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(configuration, loginService, authenticationStateDao, validator);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }
}
