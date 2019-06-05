package ee.hm.dop.rest;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.config.security.DopPrincipal;
import ee.hm.dop.config.security.DopUserDetails;
import ee.hm.dop.model.AuthenticatedUser;
import ee.hm.dop.model.User;
import ee.hm.dop.utils.ConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Slf4j
public class BaseResource {

    public static final String UTF_8 = "UTF-8";
    @Inject
    private HttpServletRequest request;
    @Inject
    private HttpServletResponse response;
    @Inject
    private Configuration configuration;

    protected User getLoggedInUser() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();
        return authenticatedUser != null ? authenticatedUser.getUser() : null;
    }

    protected AuthenticatedUser getAuthenticatedUser() {
        Object springPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(springPrincipal instanceof DopUserDetails)){
            return null;
        }
        DopPrincipal principal = ((DopUserDetails) springPrincipal).getDopPrincipal();
        return principal == null ? null : principal.getAuthenticatedUser();
    }

    public static String decode(String string) throws UnsupportedEncodingException {
        return URLDecoder.decode(string, UTF_8);
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        return response;
    }

    public ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    public ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }

    public ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    protected String getServerAddress() {
        return configuration.getString(ConfigurationProperties.SERVER_ADDRESS);
    }
}
