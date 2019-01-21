package ee.hm.dop.rest.metadata;

import ee.hm.dop.common.test.ResourceIntegrationTestBase;
import ee.hm.dop.model.enums.Role;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RoleResourceTest extends ResourceIntegrationTestBase {

    public static final String GET_ROLES = "role";

    @Test
    public void getRoles_returns_all_roles() {
        login(USER_ADMIN);

        List<Role> roles = doGet(GET_ROLES, new GenericType<List<Role>>() {
        });
        List<Role> expected = new ArrayList<>(Arrays.asList(Role.values()));
        assertEquals(expected, roles);
    }
}
