package ee.hm.dop.common.test;

import ee.hm.dop.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserValidation {

    public static void assertUser(User user, TestUser testUser, TestLayer layer) {
        assertEquals(testUser.id, user.getId());
        if (layer == TestLayer.DAO) {
            assertEquals(testUser.idCode, user.getIdCode());
        } else {
            assertNull(user.getIdCode());
        }
        assertEquals(testUser.username, user.getUsername());
        assertEquals(testUser.firstName, user.getName());
        assertEquals(testUser.lastName, user.getSurname());
    }
}
