package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.AuthenticatedUserDao;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.user.UserSession;
import ee.hm.dop.service.login.SessionUtil;
import ee.hm.dop.service.login.TokenGenerator;
import ee.hm.dop.utils.exceptions.DuplicateTokenException;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class SessionService {

    @Inject
    private AuthenticatedUserDao authenticatedUserDao;
    @Inject
    private TokenGenerator tokenGenerator;

    public AuthenticatedUser startSession(AuthenticatedUser authenticatedUser) {
        try {
            authenticatedUser.setToken(tokenGenerator.secureToken());
            return authenticatedUserDao.createAuthenticatedUser(authenticatedUser);
        } catch (DuplicateTokenException e) {
            authenticatedUser.setToken(tokenGenerator.secureToken());
            return authenticatedUserDao.createAuthenticatedUser(authenticatedUser);
        }
    }

    public AuthenticatedUser updateSession(UserSession userSession, AuthenticatedUser authenticatedUser){
        if (userSession.isContinueSession()){
            return prolongSession(authenticatedUser);
        }
        return declineSession(authenticatedUser);
    }

    public AuthenticatedUser prolongSession(AuthenticatedUser authenticatedUser){
        authenticatedUser.setSessionTime(SessionUtil.sessionTime(DateTime.now()));
        authenticatedUser.setSessionNumber(authenticatedUser.getSessionNumber() != null ? authenticatedUser.getSessionNumber() + 1 : 1);
        return authenticatedUserDao.createOrUpdate(authenticatedUser);
    }

    public AuthenticatedUser declineSession(AuthenticatedUser authenticatedUser){
        authenticatedUser.setDeclined(true);
        return authenticatedUserDao.createOrUpdate(authenticatedUser);
    }

    public void terminateSession(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser != null) {
            authenticatedUser.setSessionTime(DateTime.now());
            authenticatedUserDao.delete(authenticatedUser);
        }
    }

    public void logout(AuthenticatedUser authenticatedUser) {
        if (authenticatedUser != null) {
            authenticatedUser.setSessionTime(DateTime.now());
            authenticatedUser.setLoggedOut(true);
            authenticatedUserDao.delete(authenticatedUser);
        }
    }
}
