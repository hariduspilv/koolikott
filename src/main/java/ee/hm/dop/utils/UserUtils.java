package ee.hm.dop.utils;

import static ee.hm.dop.model.Role.ADMIN;
import ee.hm.dop.model.User;

public class UserUtils {

    public static boolean isAdmin(User user) {
        return user != null && user.getRole() == ADMIN;
    }
}
