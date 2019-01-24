package ee.hm.dop.service.login;

import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.enums.LanguageC;
import ee.hm.dop.model.mobileid.MobileIDSecurityCodes;
import ee.hm.dop.model.mobileid.soap.MobileAuthenticateResponse;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.soap.SOAPException;

import java.time.LocalDateTime;

import static ee.hm.dop.service.login.MobileIDLoginService.ESTONIAN_CALLING_CODE;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
    @Mock
    private TokenGenerator tokenGenerator;
    @Mock
    private AuthenticationStateService authenticationStateService;

    @Test
    public void authenticate() throws Exception {
        MobileAuthenticateResponse mobileAuthenticateResponse = response(ID_CODE, "JAAN,SEPP,88881010888");

        expect(mobileIDSOAPService.authenticate(PHONE_NUMBER, ID_CODE, LANGUAGE_EST)).andReturn(mobileAuthenticateResponse);

        AuthenticationState capturedAuthenticationState = authenticationState(mobileAuthenticateResponse);
        expect(authenticationStateService.save(mobileAuthenticateResponse)).andReturn(capturedAuthenticationState);

        replayAll();

        MobileIDSecurityCodes mobileIDSecurityCodes = mobileIDLoginService.authenticate(PHONE_NUMBER, ID_CODE, LANGUAGE_EST);

        verifyAll();

        validateAuthenticationState(mobileAuthenticateResponse, capturedAuthenticationState);
        validateMobileIDSecurityCodes(mobileIDSecurityCodes, mobileAuthenticateResponse, capturedAuthenticationState);
    }

    @Test
    public void authenticateWithoutCallingCode() throws Exception {
        MobileAuthenticateResponse mobileAuthenticateResponse = response(ID_CODE1, "JAAN,SEPP,11110000111");

        expect(mobileIDSOAPService.authenticate(ESTONIAN_CALLING_CODE + PHONE_NUMBER1, ID_CODE1, LANGUAGE_EST)).andReturn(
                mobileAuthenticateResponse);
        AuthenticationState capturedAuthenticationState = authenticationState(mobileAuthenticateResponse);
        expect(authenticationStateService.save(mobileAuthenticateResponse)).andReturn(capturedAuthenticationState);

        replayAll();

        MobileIDSecurityCodes mobileIDSecurityCodes = mobileIDLoginService.authenticate(PHONE_NUMBER1, ID_CODE1, LANGUAGE_EST);

        verifyAll();

        validateAuthenticationState(mobileAuthenticateResponse, capturedAuthenticationState);
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

    private AuthenticationState authenticationState() {
        AuthenticationState state = new AuthenticationState();
        state.setSessionCode(SESSION_CODE);
        state.setToken(SOME_TOKEN);
        return state;
    }

    private AuthenticationState authenticationState(MobileAuthenticateResponse response) {
        AuthenticationState state = authenticationState();
        state.setIdCode(response.getIdCode());
        state.setName(response.getName());
        state.setSurname(response.getSurname());
        state.setSessionCode(response.getSessionCode());
        state.setCreated(LocalDateTime.now());
        return state;
    }

    private static Language language(String eng) {
        Language language = new Language();
        language.setCode(eng);
        return language;
    }

    private void validateAuthenticationState(MobileAuthenticateResponse response, AuthenticationState value) {
        assertEquals(response.getIdCode(), value.getIdCode());
        assertEquals(response.getName(), value.getName());
        assertEquals(response.getSurname(), value.getSurname());
        assertEquals(response.getSessionCode(), value.getSessionCode());
        LocalDateTime created = value.getCreated();
        assertFalse(created.isAfter(LocalDateTime.now()));
    }

    private void validateMobileIDSecurityCodes(MobileIDSecurityCodes mobileIDSecurityCodes,
                                               MobileAuthenticateResponse response, AuthenticationState value) {
        assertEquals(response.getChallengeID(), mobileIDSecurityCodes.getChallengeId());
        assertNotNull(value.getToken());
        assertEquals(value.getToken(), mobileIDSecurityCodes.getToken());
    }

    private void replayAll(Object... mocks) {
        replay(authenticationStateDao, mobileIDSOAPService, authenticationStateService);

        if (mocks != null) {
            for (Object object : mocks) {
                replay(object);
            }
        }
    }

    private void verifyAll(Object... mocks) {
        verify(authenticationStateDao, mobileIDSOAPService, authenticationStateService);

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
