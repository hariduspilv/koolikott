package ee.hm.dop.service.login;

import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.mobileid.soap.MobileAuthenticateResponse;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class AuthenticationStateService {

    @Inject
    private AuthenticationStateDao authenticationStateDao;
    @Inject
    private TokenGenerator tokenGenerator;

    public AuthenticationState save(MobileAuthenticateResponse response) {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setToken(tokenGenerator.secureToken());
        authenticationState.setCreated(new DateTime());
        authenticationState.setSessionCode(response.getSessionCode());
        authenticationState.setIdCode(response.getIdCode());
        authenticationState.setName(response.getName());
        authenticationState.setSurname(response.getSurname());
        return authenticationStateDao.createAuthenticationState(authenticationState);
    }

    public AuthenticationState save(String idCode, String firstname, String surname) {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setToken(tokenGenerator.secureToken());
        authenticationState.setCreated(new DateTime());
        authenticationState.setIdCode(idCode);
        authenticationState.setName(firstname);
        authenticationState.setSurname(surname);
        return authenticationStateDao.createAuthenticationState(authenticationState);
    }
}
