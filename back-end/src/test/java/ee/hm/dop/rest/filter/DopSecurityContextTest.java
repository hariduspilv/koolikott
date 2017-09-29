package ee.hm.dop.rest.filter;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriInfo;

import ee.hm.dop.model.enums.RoleString;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class DopSecurityContextTest {

    @Mock
    private DopPrincipal dopPrincipal;

    @Mock
    private UriInfo uriInfo;

    @Test
    public void isUserInRoleNull() {
        DopSecurityContext securityContext = getDopSecurityContext(null, uriInfo);

        boolean response = securityContext.isUserInRole(RoleString.USER);

        assertFalse(response);
    }

    @Test
    public void isUserInRole() {
        DopSecurityContext securityContext = getDopSecurityContext(dopPrincipal, uriInfo);

        expect(dopPrincipal.isUserInRole(RoleString.USER)).andReturn(true);

        replay(dopPrincipal, uriInfo);

        assertTrue(securityContext.isUserInRole(RoleString.USER));

        verify(dopPrincipal, uriInfo);

    }

    @Test
    public void isSecureFalse() throws URISyntaxException {
        DopSecurityContext securityContext = getDopSecurityContext(dopPrincipal, uriInfo);
        URI uri = new URI("http://random.org");

        expect(uriInfo.getRequestUri()).andReturn(uri);

        replay(dopPrincipal, uriInfo);

        assertFalse(securityContext.isSecure());

        verify(dopPrincipal, uriInfo);
    }

    @Test
    public void isSecure() throws URISyntaxException {
        DopSecurityContext securityContext = getDopSecurityContext(dopPrincipal, uriInfo);
        URI uri = new URI("https://random.org");

        expect(uriInfo.getRequestUri()).andReturn(uri);

        replay(dopPrincipal, uriInfo);

        assertTrue(securityContext.isSecure());

        verify(dopPrincipal, uriInfo);
    }

    private DopSecurityContext getDopSecurityContext(DopPrincipal dopPrincipal, UriInfo uriInfo) {
        return new DopSecurityContext(dopPrincipal, uriInfo);
    }
}
