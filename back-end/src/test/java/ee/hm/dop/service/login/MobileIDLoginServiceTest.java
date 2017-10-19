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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        String phoneNumber = "+37255501234";
        String idCode = "88881010888";
        Language language = new Language();
        language.setCode(LanguageC.EST);

        MobileAuthenticateResponse mobileAuthenticateResponse = new MobileAuthenticateResponse();
        mobileAuthenticateResponse.setSessionCode("789560251");
        mobileAuthenticateResponse.setStatus("OK");
        mobileAuthenticateResponse.setIdCode(idCode);
        mobileAuthenticateResponse.setName("Jaan");
        mobileAuthenticateResponse.setSurname("Sepp");
        mobileAuthenticateResponse.setCountry("EE");
        mobileAuthenticateResponse.setUserCommonName("JAAN,SEPP,88881010888");
        mobileAuthenticateResponse.setChallengeID("4321");
=======
        MobileAuthenticateResponse mobileAuthenticateResponse = response(ID_CODE, "JAAN,SEPP,88881010888");
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java

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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        String phoneNumber = "5554321";
        String idCode = "11110000111";
        Language language = new Language();
        language.setCode(LanguageC.EST);
=======
        MobileAuthenticateResponse mobileAuthenticateResponse = response(ID_CODE1, "JAAN,SEPP,11110000111");
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java

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
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        String phoneNumber = "+33355501234";
        String idCode = "99991010888";
        Language language = new Language();
        language.setCode(LanguageC.ENG);

=======
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        replayAll();
        MobileIDSecurityCodes mobileIDSecurityCodes = mobileIDLoginService.authenticate(PHONE_NUMBER2, ID_CODE2, LANGUAGE_ENG);
        verifyAll();
        assertNull(mobileIDSecurityCodes);
    }

    @Test
    public void isAuthenticated() throws SOAPException {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        String token = "someTOKEN";

        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode("2835728357835");
        authenticationState.setToken(token);

        expect(authenticationStateDao.findAuthenticationStateByToken(token)).andReturn(authenticationState);
=======
        AuthenticationState authenticationState = authenticationState();
        expect(authenticationStateDao.findAuthenticationStateByToken(SOME_TOKEN)).andReturn(authenticationState);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        expect(mobileIDSOAPService.isAuthenticated(authenticationState)).andReturn(true);
        replayAll();
        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(SOME_TOKEN);
        verifyAll();
        assertTrue(isAuthenticated);
    }

    @Test
    public void isAuthenticatedFalse() throws SOAPException {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        String token = "someTOKEN";

        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setSessionCode("2835728357835");
        authenticationState.setToken(token);

        expect(authenticationStateDao.findAuthenticationStateByToken(token)).andReturn(authenticationState);
=======
        AuthenticationState authenticationState = authenticationState();
        expect(authenticationStateDao.findAuthenticationStateByToken(SOME_TOKEN)).andReturn(authenticationState);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        expect(mobileIDSOAPService.isAuthenticated(authenticationState)).andReturn(false);
        replayAll();
        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(SOME_TOKEN);
        verifyAll();
        assertFalse(isAuthenticated);
    }

    @Test
    public void isAuthenticatedInvalidToken() throws SOAPException {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        String token = "invalidTOKEN";

        expect(authenticationStateDao.findAuthenticationStateByToken(token)).andReturn(null);

=======
        expect(authenticationStateDao.findAuthenticationStateByToken(INVALID_TOKEN)).andReturn(null);
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        replayAll();
        boolean isAuthenticated = mobileIDLoginService.isAuthenticated(INVALID_TOKEN);
        verifyAll();
        assertFalse(isAuthenticated);
    }

    private void expectCreateAuthenticationState(Capture<AuthenticationState> capturedAuthenticationState) {
<<<<<<< HEAD:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
        // Using .andReturn(capturedAuthenticationState.getValue()) would give
        // error saying "Nothing captured yet"
        expect(authenticationStateDao.createAuthenticationState(EasyMock.capture(capturedAuthenticationState)))
                .andAnswer(new IAnswer<AuthenticationState>() {
                    @Override
                    public AuthenticationState answer() throws Throwable {
                        return capturedAuthenticationState.getValue();
                    }
                });
=======
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
>>>>>>> new-develop:back-end/src/test/java/ee/hm/dop/service/login/MobileIDLoginServiceTest.java
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
