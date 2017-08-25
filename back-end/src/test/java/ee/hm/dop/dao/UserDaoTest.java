package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.User;
import org.junit.Test;

public class UserDaoTest extends DatabaseTestBase {

    @Inject
    private UserDao userDao;

    @Test
    public void findUserByIdCode() {
        User user = userDao.findUserByIdCode("39011220011");
        assertEquals("39011220011", user.getIdCode());
        assertValidUser(user);

        user = userDao.findUserByIdCode("39011220011");
        assertEquals("39011220011", user.getIdCode());
        assertValidUser(user);

        user = userDao.findUserByIdCode("39011220011");
        assertEquals("39011220011", user.getIdCode());
        assertValidUser(user);
    }

    @Test
    public void findByUsername() {
        User user = userDao.findUserByUsername("mati.maasikas");
        assertEquals(Long.valueOf(1), user.getId());
        assertEquals("mati.maasikas", user.getUsername());
        assertEquals("Mati", user.getName());
        assertEquals("Maasikas", user.getSurname());
        assertEquals("39011220011", user.getIdCode());

        user = userDao.findUserByUsername("peeter.paan");
        assertEquals(Long.valueOf(2), user.getId());
        assertEquals("peeter.paan", user.getUsername());
        assertEquals("Peeter", user.getName());
        assertEquals("Paan", user.getSurname());
        assertEquals("38011550077", user.getIdCode());

        user = userDao.findUserByUsername("voldemar.vapustav");
        assertEquals(Long.valueOf(3), user.getId());
        assertEquals("voldemar.vapustav", user.getUsername());
        assertEquals("Voldemar", user.getName());
        assertEquals("Vapustav", user.getSurname());
        assertEquals("37066990099", user.getIdCode());
    }

    private void assertValidUser(User user) {
        assertNotNull(user.getId());

        switch (user.getIdCode()) {
            case "39011220011":
                assertEquals("mati.maasikas", user.getUsername());
                assertEquals("Mati", user.getName());
                assertEquals("Maasikas", user.getSurname());
                break;
            case "38011550077":
                assertEquals("peeter.paan", user.getUsername());
                assertEquals("Peeter", user.getName());
                assertEquals("Paan", user.getSurname());
                break;
            case "37066990099":
                assertEquals("voldemar.vapustav", user.getUsername());
                assertEquals("Voldemar", user.getName());
                assertEquals("Vapustav", user.getSurname());
                assertEquals("37066990099", user.getIdCode());
                break;
        }
    }

    @Test
    public void countUsersWithSameUsernameIgnoringAccents() {
        assertEquals(Long.valueOf(2), userDao.countUsersWithSameUsername("mati.maasikas"));
    }

    @Test
    public void countUsersWithSameUsernameNoResults() {
        assertEquals(Long.valueOf(0), userDao.countUsersWithSameUsername("there.is.no.such.username"));
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

        assertEquals(2, restrictedUsers.size());
    }

    @Test
    public void getModerators() {
        List<User> moderators = userDao.getUsersByRole(Role.MODERATOR);

        assertEquals(1, moderators.size());
    }

    @Test
    public void getAllUsers() {
        List<User> allUsers = userDao.findAll();
        assertEquals(15, allUsers.size());
    }

    private User getUser() {
        User user = new User();
        user.setName("Mati2");
        user.setSurname("Maasikas2");
        user.setUsername("mati2.maasikas2");
        user.setIdCode("12345678969");
        return user;
    }
}
