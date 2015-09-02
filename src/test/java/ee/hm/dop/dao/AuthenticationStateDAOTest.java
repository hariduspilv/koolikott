package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticationState;

public class AuthenticationStateDAOTest extends DatabaseTestBase {

    @Inject
    private AuthenticationStateDAO authenticationStateDAO;

    @Test
    public void createAuthenticationState() {
        AuthenticationState authenticationState = getAuthenticationState();

        AuthenticationState returnedAuthenticationState = authenticationStateDAO
                .createAuthenticationState(authenticationState);

        assertEquals(returnedAuthenticationState.getToken(), authenticationState.getToken());

        authenticationStateDAO.delete(returnedAuthenticationState);
    }

    @Test
    public void createAuthenticationStateSameToken() {
        AuthenticationState authenticationState = getAuthenticationState();

        AuthenticationState returnedAuthenticationState = authenticationStateDAO
                .createAuthenticationState(authenticationState);

        AuthenticationState authenticationState2 = new AuthenticationState();
        authenticationState2.setToken("superTOKEN");

        try {
            authenticationStateDAO.createAuthenticationState(authenticationState);
            fail("Exception expected");
        } catch (DuplicateTokenException e) {
            // expected
        }

        authenticationStateDAO.delete(returnedAuthenticationState);
    }

    @Test
    public void findAuthenticationStateByToken() {
        String token = "testTOKEN";

        AuthenticationState authenticationState = authenticationStateDAO.findAuthenticationStateByToken(token);
        assertEquals(token, authenticationState.getToken());
    }

    @Test
    public void delete() {
        AuthenticationState authenticationState = getAuthenticationState();

        AuthenticationState returnedAuthenticationState = authenticationStateDAO
                .createAuthenticationState(authenticationState);

        authenticationStateDAO.delete(returnedAuthenticationState);
        assertNull(authenticationStateDAO.findAuthenticationStateByToken("superTOKEN"));
    }

    private AuthenticationState getAuthenticationState() {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setToken("superTOKEN");
        return authenticationState;
    }

}
