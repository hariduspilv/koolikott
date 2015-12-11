package ee.hm.dop.rest.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMockRunner;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;

@RunWith(EasyMockRunner.class)
public class DopPrincipalTest {

    AuthenticatedUser authenticatedUser = getUser();

    @TestSubject
    private DopPrincipal dopPrincipal = new DopPrincipal(authenticatedUser);

    @Test
    public void getName() {
        assertEquals("Mati Maasikas", dopPrincipal.getName());
    }

    @Test
    public void getToken() {
        assertNull(dopPrincipal.getSecurityToken());
    }

    @Test
    public void isUserInRole() {
        assertTrue(dopPrincipal.isUserInRole("USER"));
    }

    private AuthenticatedUser getUser() {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        User user = new User();
        user.setName("Mati");
        user.setSurname("Maasikas");
        user.setRole(Role.USER);
        authenticatedUser.setUser(user);

        return authenticatedUser;
    }
}
