package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.exceptions.DuplicateUserException;
import ee.hm.dop.model.User;

public class UserDAOTest extends DatabaseTestBase {

    @Inject
    private UserDAO userDAO;

    @Test
    public void findUserByIdCode() {
        User user = userDAO.findUserByIdCode("39011220011");
        assertEquals("39011220011", user.getIdCode());
        assertValidUser(user);

        user = userDAO.findUserByIdCode("39011220011");
        assertEquals("39011220011", user.getIdCode());
        assertValidUser(user);

        user = userDAO.findUserByIdCode("39011220011");
        assertEquals("39011220011", user.getIdCode());
        assertValidUser(user);
    }

    @Test
    public void findByUsername() {
        User user = userDAO.findUserByUsername("mati.maasikas");
        assertEquals(Long.valueOf(1), user.getId());
        assertEquals("mati.maasikas", user.getUsername());
        assertEquals("Mati", user.getName());
        assertEquals("Maasikas", user.getSurname());
        assertEquals("39011220011", user.getIdCode());

        user = userDAO.findUserByUsername("peeter.paan");
        assertEquals(Long.valueOf(2), user.getId());
        assertEquals("peeter.paan", user.getUsername());
        assertEquals("Peeter", user.getName());
        assertEquals("Paan", user.getSurname());
        assertEquals("38011550077", user.getIdCode());

        user = userDAO.findUserByUsername("voldemar.vapustav");
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
    public void countUsersWithSameFullName() {
        assertEquals(Long.valueOf(1), userDAO.countUsersWithSameFullName("Mati", "Maasikas"));
        assertEquals(Long.valueOf(1), userDAO.countUsersWithSameFullName("Peeter", "Paan"));
        assertEquals(Long.valueOf(2), userDAO.countUsersWithSameFullName("Voldemar", "Vapustav"));
    }

    @Test
    public void createUserWithDuplicateUsername() {
        User user = new User();
        user.setName("Mati");
        user.setSurname("Maasikas");
        user.setUsername("mati.maasikas");
        user.setIdCode("12345678901");
        try {
            userDAO.createUser(user);
            fail("Exception expected. ");
        } catch (DuplicateUserException e) {
            // expected
        }
    }

}
