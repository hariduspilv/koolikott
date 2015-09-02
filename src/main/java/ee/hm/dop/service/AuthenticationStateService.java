package ee.hm.dop.service;

import com.google.inject.Inject;

import ee.hm.dop.dao.AuthenticationStateDAO;
import ee.hm.dop.model.AuthenticationState;

public class AuthenticationStateService {

    @Inject
    private AuthenticationStateDAO authenticationStateDAO;

    public AuthenticationState create(AuthenticationState authenticationState) {
        return authenticationStateDAO.createAuthenticationState(authenticationState);
    }

    public AuthenticationState getAuthenticationStateByToken(String token) {
        return authenticationStateDAO.findAuthenticationStateByToken(token);
    }

    public void delete(AuthenticationState authenticationState) {
        authenticationStateDAO.delete(authenticationState);
    }

}
