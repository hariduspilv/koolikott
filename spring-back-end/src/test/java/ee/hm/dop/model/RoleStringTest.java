package ee.hm.dop.model;

import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.RoleString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RoleStringTest {
    @Test
    public void roleAndRoleStringAreTheSame() throws Exception {
        assertEquals(Role.USER, Role.valueOf(RoleString.USER.substring(5)));
        assertEquals(Role.ADMIN, Role.valueOf(RoleString.ADMIN.substring(5)));
        assertEquals(Role.MODERATOR, Role.valueOf(RoleString.MODERATOR.substring(5)));
        assertEquals(Role.RESTRICTED, Role.valueOf(RoleString.RESTRICTED.substring(5)));
        assertEquals(Role.USER.name(), RoleString.USER.substring(5));
        assertEquals(Role.ADMIN.name(), RoleString.ADMIN.substring(5));
        assertEquals(Role.MODERATOR.name(), RoleString.MODERATOR.substring(5));
        assertEquals(Role.RESTRICTED.name(), RoleString.RESTRICTED.substring(5));
    }
}