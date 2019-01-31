package ee.netgroup.htm.framework.api;

import ee.netgroup.htm.api.DevLoginResponse;
import ee.netgroup.htm.api.EkoolikottApi;
import org.testng.annotations.Test;

import static ee.netgroup.htm.api.UserRole.ADMIN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EkoolikottApiTests {

    @Test
    public void canChangeUserRole(){
        DevLoginResponse userData = EkoolikottApi.setUserRole("39012050266", ADMIN);
        assert userData != null;
        assertThat(userData.getAuthenticatedUserDto().getUserDto().getRole(), is(ADMIN));
    }
}
