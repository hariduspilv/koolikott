package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.User;

public class UserDAOTest extends DatabaseTestBase {

    @Inject
    private UserDAO userDAO;

    @Test
    public void findUserByIdCode() {
        assertValidUser(userDAO.findUserByIdCode("39011220011"));
        assertValidUser(userDAO.findUserByIdCode("38011550077"));
        assertValidUser(userDAO.findUserByIdCode("37066990099"));
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

        if (user.getId() == 1) {
            assertEquals("mati.maasikas", user.getUsername());
            assertEquals("Mati", user.getName());
            assertEquals("Maasikas", user.getSurname());
            assertEquals("39011220011", user.getIdCode());
        } else if (user.getId() == 2) {
            assertEquals("peeter.paan", user.getUsername());
            assertEquals("Peeter", user.getName());
            assertEquals("Paan", user.getSurname());
            assertEquals("38011550077", user.getIdCode());
        } else if (user.getId() == 3) {
            assertEquals("voldemar.vapustav", user.getUsername());
            assertEquals("Voldemar", user.getName());
            assertEquals("Vapustav", user.getSurname());
            assertEquals("37066990099", user.getIdCode());
        }
    }

}
