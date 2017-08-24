package ee.hm.dop.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class RoleStringTest {
    @Test
    public void roleAndRoleStringAreTheSame() throws Exception {
        assertEquals(Role.USER, Role.valueOf(RoleString.USER));
        assertEquals(Role.ADMIN, Role.valueOf(RoleString.ADMIN));
        assertEquals(Role.MODERATOR, Role.valueOf(RoleString.MODERATOR));
        assertEquals(Role.RESTRICTED, Role.valueOf(RoleString.RESTRICTED));
        assertEquals(Role.USER.name(), RoleString.USER);
        assertEquals(Role.ADMIN.name(), RoleString.ADMIN);
        assertEquals(Role.MODERATOR.name(), RoleString.MODERATOR);
        assertEquals(Role.RESTRICTED.name(), RoleString.RESTRICTED);
    }
}