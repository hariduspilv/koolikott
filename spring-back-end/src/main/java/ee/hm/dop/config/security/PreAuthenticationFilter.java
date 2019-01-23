package ee.hm.dop.config.security;

import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.service.login.SessionUtil;
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Component
public class PreAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    public PreAuthenticationFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String token = request.getHeader("Authentication");
        if (token == null) {
            return null;
        }
        AuthenticatedUser authenticatedUser = authenticatedUserService.getAuthenticatedUserByToken(token);
        if (authenticatedUser == null) {
            userHasAlreadyLoggedOut();
            return null;
        }
        String username = authenticatedUser.getUser().getUsername();
        if (!username.equals(request.getHeader("Username"))) {
            requestHeaderAndUsernameDontMatch(username);
            return null;
        }
        if (SessionUtil.sessionInValid(authenticatedUser)) {
            sessionExpired(username);
            return null;
        }

        return new DopPrincipal(authenticatedUser);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    private void sessionExpired(String username) {
        logger.error("session has expired" + username);
    }

    private void requestHeaderAndUsernameDontMatch(String username) {
        logger.error("user request header and username do not match: " + username);
    }

    private void userHasAlreadyLoggedOut() {
        logger.error("user has already logged out");
    }
}