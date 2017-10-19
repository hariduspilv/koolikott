package ee.hm.dop.dao;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.common.test.TestConstants;
<<<<<<< HEAD
=======
import ee.hm.dop.common.test.TestLayer;
>>>>>>> new-develop
import ee.hm.dop.common.test.TestUser;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.List;

import static org.junit.Assert.*;

public class UserDaoTest extends DatabaseTestBase {

    public static final String USERNAME_NOT_EXISTING = "there.is.no.such.username";
    @Inject
    private UserDao userDao;

    @Test
    public void findUserByIdCode() {
<<<<<<< HEAD
        User user = findByIdCode(TestConstants.USER_MATI);
        validateUser(user, TestConstants.USER_MATI);

        user = findByIdCode(TestConstants.USER_PEETER);
        validateUser(user, TestConstants.USER_PEETER);

        user = findByIdCode(TestConstants.USER_VOLDERMAR);
        validateUser(user, TestConstants.USER_VOLDERMAR);
=======
        User user = findByIdCode(USER_MATI);
        validateUser(user, USER_MATI, TestLayer.DAO);

        user = findByIdCode(USER_PEETER);
        validateUser(user, USER_PEETER, TestLayer.DAO);

        user = findByIdCode(USER_VOLDERMAR);
        validateUser(user, USER_VOLDERMAR, TestLayer.DAO);
>>>>>>> new-develop
    }

    @Test
    public void findByUsername() {
<<<<<<< HEAD
        User user = findByName(TestConstants.USER_MATI);
        validateUser(user, TestConstants.USER_MATI);

        user = findByName(TestConstants.USER_PEETER);
        validateUser(user, TestConstants.USER_PEETER);

        user = findByName(TestConstants.USER_VOLDERMAR);
        validateUser(user, TestConstants.USER_VOLDERMAR);
=======
        User user = findByName(USER_MATI);
        validateUser(user, USER_MATI, TestLayer.DAO);

        user = findByName(USER_PEETER);
        validateUser(user, USER_PEETER, TestLayer.DAO);

        user = findByName(USER_VOLDERMAR);
        validateUser(user, USER_VOLDERMAR, TestLayer.DAO);
>>>>>>> new-develop
    }

    @Test
    public void countUsersWithSameUsernameIgnoringAccents() {
<<<<<<< HEAD
        assertEquals(Long.valueOf(2), userDao.countUsersWithSameUsername(TestConstants.USER_MATI.username));
=======
        assertEquals(Long.valueOf(2), userDao.countUsersWithSameUsername(USER_MATI.username));
>>>>>>> new-develop
    }

    @Test
    public void countUsersWithSameUsernameNoResults() {
        assertEquals(Long.valueOf(0), userDao.countUsersWithSameUsername(USERNAME_NOT_EXISTING));
    }

    @Test
    public void updateUserWithDuplicateUsername() {
        User user = new User();
        user.setName("Mati");
        user.setSurname("Maasikas");
        user.setUsername("mati.maasikas");
        user.setIdCode("12345678901");
        try {
            userDao.createOrUpdate(user);
            fail("Exception expected. ");
        } catch (PersistenceException e) {
            // expected
        }
    }

    @Test
    public void update() {
        User user = getUser();
        User returnedUser = userDao.createOrUpdate(user);
        User foundUser = userDao.findUserByIdCode(user.getIdCode());

        assertEquals(user.getUsername(), foundUser.getUsername());
        assertEquals(user.getIdCode(), returnedUser.getIdCode());
        userDao.delete(returnedUser);
    }

    @Test
    public void delete() {
        User user = getUser();
        User returnedUser = userDao.createOrUpdate(user);
        userDao.delete(returnedUser);
        assertNull(userDao.findUserByIdCode(user.getIdCode()));
    }

    @Test
    public void getRestrictedUsers() {
        List<User> restrictedUsers = userDao.getUsersByRole(Role.RESTRICTED);
        assertTrue(CollectionUtils.isNotEmpty(restrictedUsers));
    }

    @Test
    public void getModerators() {
        List<User> moderators = userDao.getUsersByRole(Role.MODERATOR);
        assertTrue(CollectionUtils.isNotEmpty(moderators));
    }

    @Test
    public void getAllUsers() {
        List<User> allUsers = userDao.findAll();
        assertTrue(CollectionUtils.isNotEmpty(allUsers));
    }

    private User getUser() {
        User user = new User();
        user.setName("Mati2");
        user.setSurname("Maasikas2");
        user.setUsername("mati2.maasikas2");
        user.setIdCode("12345678969");
        return user;
    }

    private User findByIdCode(TestUser testUser) {
        return userDao.findUserByIdCode(testUser.idCode);
    }

    private User findByName(TestUser testUser) {
        return userDao.findUserByUsername(testUser.username);
    }
<<<<<<< HEAD

    private void validateUser(User user, TestUser testUser) {
        assertEquals(testUser.id, user.getId());
        assertEquals(testUser.idCode, user.getIdCode());
        assertEquals(testUser.username, user.getUsername());
        assertEquals(testUser.firstName, user.getName());
        assertEquals(testUser.lastName, user.getSurname());
    }
=======
>>>>>>> new-develop
}
