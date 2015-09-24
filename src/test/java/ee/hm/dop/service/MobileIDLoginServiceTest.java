package ee.hm.dop.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.soap.SOAPException;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.IAnswer;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.mobileid.MobileAuthenticateResponse;
import ee.hm.dop.service.MobileIDLoginService.MobileAuthResponse;

@RunWith(EasyMockRunner.class)
public class MobileIDLoginServiceTest {

    @TestSubject
    private MobileIDLoginService mobileIDLoginService = new MobileIDLoginService();

    @Mock
    private MobileIDSOAPService mobileIDSOAPService;

    @Mock
    private AuthenticationStateDAO authenticationStateDAO;

    @Test
    public void authenticate() throws Exception {
        String phoneNumber = "55501234";
        String idCode = "88881010888";
        Language language = new Language();
        language.setCode("est");

        MobileAuthenticateResponse mobileAuthenticateResponse = new MobileAuthenticateResponse();
        mobileAuthenticateResponse.setSessionCode("789560251");
        mobileAuthenticateResponse.setStatus("OK");
        mobileAuthenticateResponse.setIdCode(idCode);
        mobileAuthenticateResponse.setName("Jaan");
        mobileAuthenticateResponse.setSurname("Sepp");
        mobileAuthenticateResponse.setCountry("EE");
        mobileAuthenticateResponse.setUserCommonName("JAAN,SEPP,88881010888");
        mobileAuthenticateResponse.setChallengeID("4321");

        expect(mobileIDSOAPService.authenticate(phoneNumber, idCode, language)).andReturn(mobileAuthenticateResponse);

        Capture<AuthenticationState> capturedAuthenticationState = newCapture();
        expectCreateAuthenticationState(capturedAuthenticationState);

        replayAll();

        MobileAuthResponse mobileAuthResponse = mobileIDLoginService.authenticate(phoneNumber, idCode, language);

        verifyAll();

        validateAuthenticationState(capturedAuthenticationState, mobileAuthenticateResponse);
        validateMobileAuthResponse(mobileAuthResponse, mobileAuthenticateResponse, capturedAuthenticationState);
    }

    @Test
    public void isAuthenticated() throws SOAPException {
        String token = "someTOKEN";

        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode("2835728357835");
        authenticationState.setToken(token);

        expect(authenticationStateDAO.findAuthenticationStateByToken(token)).andReturn(authenticationState);
        expect(mobileIDSOAPService.isAuthenticated(authenticationState)).andReturn(true);

        replayAll();

        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(token);

        verifyAll();

        assertTrue(isAuthenticated);
    }

    @Test
    public void isAuthenticatedFalse() throws SOAPException {
        String token = "someTOKEN";

        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode("2835728357835");
        authenticationState.setToken(token);

        expect(authenticationStateDAO.findAuthenticationStateByToken(token)).andReturn(authenticationState);
        expect(mobileIDSOAPService.isAuthenticated(authenticationState)).andReturn(false);

        replayAll();

        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(token);

        verifyAll();

        assertFalse(isAuthenticated);
    }

    @Test
    public void isAuthenticatedInvalidToken() throws SOAPException {
        String token = "invalidTOKEN";

        expect(authenticationStateDAO.findAuthenticationStateByToken(token)).andReturn(null);

        replayAll();

        try {
            mobileIDLoginService.isAuthenticated(token);
            fail("Exception expected.");
        } catch (RuntimeException e) {
            assertEquals("Invalid token.", e.getMessage());
        }

        verifyAll();
    }

    private void expectCreateAuthenticationState(Capture<AuthenticationState> capturedAuthenticationState) {
        // Using .andReturn(capturedAuthenticationState.getValue()) would give
        // error saying "Nothing captured yet"
        expect(authenticationStateDAO.createAuthenticationState(EasyMock.capture(capturedAuthenticationState)))
                .andAnswer(new IAnswer<AuthenticationState>() {
                    @Override
                    public AuthenticationState answer() throws Throwable {
                        return capturedAuthenticationState.getValue();
                    }
                });
    }

    private void validateAuthenticationState(Capture<AuthenticationState> capturedAuthenticationState,
            MobileAuthenticateResponse response) {
        assertEquals(response.getIdCode(), capturedAuthenticationState.getValue().getIdCode());
        assertEquals(response.getName(), capturedAuthenticationState.getValue().getName());
        assertEquals(response.getSurname(), capturedAuthenticationState.getValue().getSurname());
        assertEquals(response.getSessionCode(), capturedAuthenticationState.getValue().getSessionCode());
        DateTime created = capturedAuthenticationState.getValue().getCreated();
        assertFalse(created.isAfterNow());
    }

    private void validateMobileAuthResponse(MobileAuthResponse mobileAuthResponse, MobileAuthenticateResponse response,
            Capture<AuthenticationState> capturedAuthenticationState) {
        assertEquals(response.getChallengeID(), mobileAuthResponse.getChallengeId());
        assertNotNull(capturedAuthenticationState.getValue().getToken());
        assertEquals(capturedAuthenticationState.getValue().getToken(), mobileAuthResponse.getToken());
    }

    private void replayAll(Object... mocks) {
        replay(authenticationStateDAO, mobileIDSOAPService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(authenticationStateDAO, mobileIDSOAPService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

}
