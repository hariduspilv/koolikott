package ee.hm.dop.service.login;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import ee.hm.dop.dao.AuthenticatedUserDao;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.login.LogoutService;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class LogoutServiceTest {

    @TestSubject
    private LogoutService logoutService = new LogoutService();

    @Mock
    private AuthenticatedUserDao authenticatedUserDao;

    @Test
    public void logout() {
        AuthenticatedUser authenticatedUser = createMock(AuthenticatedUser.class);

        authenticatedUserDao.delete(authenticatedUser);
        expectLastCall();

        replay(authenticatedUserDao);

        logoutService.logout(authenticatedUser);

        verify(authenticatedUserDao);
    }

    @Test
    public void logoutNoUser() {
        replay(authenticatedUserDao);

        logoutService.logout(null);

        verify(authenticatedUserDao);
    }
}
