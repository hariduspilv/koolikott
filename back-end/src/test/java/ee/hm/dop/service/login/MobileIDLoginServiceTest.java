package ee.hm.dop.service.login;

import static ee.hm.dop.service.login.MobileIDLoginService.ESTONIAN_CALLING_CODE;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.xml.soap.SOAPException;

import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import ee.hm.dop.model.mobileid.soap.MobileAuthenticateResponse;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.IAnswer;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class MobileIDLoginServiceTest {

    public static final String PHONE_NUMBER2 = "+33355501234";
    public static final String PHONE_NUMBER = "+37255501234";
    public static final String PHONE_NUMBER1 = "5554321";
    public static final String ID_CODE = "88881010888";
    public static final String ID_CODE1 = "11110000111";
    public static final String ID_CODE2 = "99991010888";
    public static final String INVALID_TOKEN = "invalidTOKEN";
    public static final String SESSION_CODE = "2835728357835";
    public static final String SOME_TOKEN = "someTOKEN";
    public static final Language LANGUAGE_EST = language(LanguageC.EST);
    public static final Language LANGUAGE_ENG = language(LanguageC.ENG);
    @TestSubject
    private MobileIDLoginService mobileIDLoginService = new MobileIDLoginService();
    @Mock
    private MobileIDSOAPService mobileIDSOAPService;
    @Mock
    private AuthenticationStateDao authenticationStateDao;

    @Test
    public void authenticate() throws Exception {
        MobileAuthenticateResponse mobileAuthenticateResponse = response(ID_CODE, "JAAN,SEPP,88881010888");

        expect(mobileIDSOAPService.authenticate(PHONE_NUMBER, ID_CODE, LANGUAGE_EST)).andReturn(mobileAuthenticateResponse);

        Capture<AuthenticationState> capturedAuthenticationState = newCapture();
        expectCreateAuthenticationState(capturedAuthenticationState);

        replayAll();

        MobileIDSecurityCodes mobileIDSecurityCodes = mobileIDLoginService.authenticate(PHONE_NUMBER, ID_CODE, LANGUAGE_EST);

        verifyAll();

        validateAuthenticationState(capturedAuthenticationState, mobileAuthenticateResponse);
        validateMobileIDSecurityCodes(mobileIDSecurityCodes, mobileAuthenticateResponse, capturedAuthenticationState);
    }

    @Test
    public void authenticateWithoutCallingCode() throws Exception {
        MobileAuthenticateResponse mobileAuthenticateResponse = response(ID_CODE1, "JAAN,SEPP,11110000111");

        expect(mobileIDSOAPService.authenticate(ESTONIAN_CALLING_CODE + PHONE_NUMBER1, ID_CODE1, LANGUAGE_EST)).andReturn(
                mobileAuthenticateResponse);

        Capture<AuthenticationState> capturedAuthenticationState = newCapture();
        expectCreateAuthenticationState(capturedAuthenticationState);

        replayAll();

        MobileIDSecurityCodes mobileIDSecurityCodes = mobileIDLoginService.authenticate(PHONE_NUMBER1, ID_CODE1, LANGUAGE_EST);

        verifyAll();

        validateAuthenticationState(capturedAuthenticationState, mobileAuthenticateResponse);
        validateMobileIDSecurityCodes(mobileIDSecurityCodes, mobileAuthenticateResponse, capturedAuthenticationState);
    }

    @Test
    public void authenticateNonEstonianPhoneNumber() throws Exception {
        replayAll();
        MobileIDSecurityCodes mobileIDSecurityCodes = mobileIDLoginService.authenticate(PHONE_NUMBER2, ID_CODE2, LANGUAGE_ENG);
        verifyAll();
        assertNull(mobileIDSecurityCodes);
    }

    @Test
    public void isAuthenticated() throws SOAPException {
        AuthenticationState authenticationState = authenticationState();
        expect(authenticationStateDao.findAuthenticationStateByToken(SOME_TOKEN)).andReturn(authenticationState);
        expect(mobileIDSOAPService.isAuthenticated(authenticationState)).andReturn(true);
        replayAll();
        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(SOME_TOKEN);
        verifyAll();
        assertTrue(isAuthenticated);
    }

    @Test
    public void isAuthenticatedFalse() throws SOAPException {
        AuthenticationState authenticationState = authenticationState();
        expect(authenticationStateDao.findAuthenticationStateByToken(SOME_TOKEN)).andReturn(authenticationState);
        expect(mobileIDSOAPService.isAuthenticated(authenticationState)).andReturn(false);
        replayAll();
        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(SOME_TOKEN);
        verifyAll();
        assertFalse(isAuthenticated);
    }

    @Test
    public void isAuthenticatedInvalidToken() throws SOAPException {
        expect(authenticationStateDao.findAuthenticationStateByToken(INVALID_TOKEN)).andReturn(null);
        replayAll();
        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(INVALID_TOKEN);
        verifyAll();
        assertFalse(isAuthenticated);
    }

    private void expectCreateAuthenticationState(Capture<AuthenticationState> capturedAuthenticationState) {
        expect(authenticationStateDao.createAuthenticationState(EasyMock.capture(capturedAuthenticationState)))
                .andAnswer(capturedAuthenticationState::getValue);
    }

    private AuthenticationState authenticationState() {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode(SESSION_CODE);
        authenticationState.setToken(SOME_TOKEN);
        return authenticationState;
    }

    private static Language language(String eng) {
        Language language = new Language();
        language.setCode(eng);
        return language;
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

    private void validateMobileIDSecurityCodes(MobileIDSecurityCodes mobileIDSecurityCodes,
            MobileAuthenticateResponse response, Capture<AuthenticationState> capturedAuthenticationState) {
        assertEquals(response.getChallengeID(), mobileIDSecurityCodes.getChallengeId());
        assertNotNull(capturedAuthenticationState.getValue().getToken());
        assertEquals(capturedAuthenticationState.getValue().getToken(), mobileIDSecurityCodes.getToken());
    }

    private void replayAll(Object... mocks) {
        replay(authenticationStateDao, mobileIDSOAPService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(authenticationStateDao, mobileIDSOAPService);

        if (mocks != null) {
            for (Object object : mocks) {
                verify(object);
            }
        }
    }

    private MobileAuthenticateResponse response(String idCode, String userCommonName) {
        MobileAuthenticateResponse mobileAuthenticateResponse = new MobileAuthenticateResponse();
        mobileAuthenticateResponse.setSessionCode("789560251");
        mobileAuthenticateResponse.setStatus("OK");
        mobileAuthenticateResponse.setIdCode(idCode);
        mobileAuthenticateResponse.setName("Jaan");
        mobileAuthenticateResponse.setSurname("Sepp");
        mobileAuthenticateResponse.setCountry("EE");
        mobileAuthenticateResponse.setUserCommonName(userCommonName);
        mobileAuthenticateResponse.setChallengeID("4321");
        return mobileAuthenticateResponse;
    }
}
