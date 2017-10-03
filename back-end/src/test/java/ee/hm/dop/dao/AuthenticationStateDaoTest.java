package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import ee.hm.dop.model.AuthenticationState;
import org.joda.time.DateTime;
import org.junit.Test;

public class AuthenticationStateDaoTest extends DatabaseTestBase {

    public static final String SUPER_TOKEN = "superTOKEN";
    public static final String TEST_TOKEN = "testTOKEN";
    @Inject
    private AuthenticationStateDao authenticationStateDao;

    @Test
    public void createAuthenticationState() {
        AuthenticationState authenticationState = getAuthenticationState();
        AuthenticationState returnedAuthenticationState = authenticationStateDao.createAuthenticationState(authenticationState);

        assertEquals(returnedAuthenticationState.getToken(), authenticationState.getToken());
        authenticationStateDao.delete(returnedAuthenticationState);
    }

    @Test
    public void createAuthenticationStateSameToken() {
        AuthenticationState authenticationState = getAuthenticationState();
        AuthenticationState returnedAuthenticationState = authenticationStateDao.createAuthenticationState(authenticationState);

        AuthenticationState authenticationState2 = new AuthenticationState();
        authenticationState2.setToken(SUPER_TOKEN);

        try {
            authenticationStateDao.createAuthenticationState(authenticationState);
            fail("Exception expected");
        } catch (DuplicateTokenException e) {
            // expected
        }

        authenticationStateDao.delete(returnedAuthenticationState);
    }

    @Test
    public void findAuthenticationStateByToken() {
        String token = TEST_TOKEN;
        AuthenticationState authenticationState = authenticationStateDao.findAuthenticationStateByToken(token);
        assertEquals(token, authenticationState.getToken());
    }

    @Test
    public void delete() {
        AuthenticationState authenticationState = getAuthenticationState();
        AuthenticationState returnedAuthenticationState = authenticationStateDao.createAuthenticationState(authenticationState);

        authenticationStateDao.delete(returnedAuthenticationState);
        assertNull(authenticationStateDao.findAuthenticationStateByToken(SUPER_TOKEN));
    }

    private AuthenticationState getAuthenticationState() {
        AuthenticationState authenticationState = new AuthenticationState();
        authenticationState.setCreated(new DateTime());
        authenticationState.setToken(SUPER_TOKEN);
        return authenticationState;
    }

}
