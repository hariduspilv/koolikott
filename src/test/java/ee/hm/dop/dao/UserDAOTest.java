package ee.hm.dop.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import ee.hm.dop.common.test.DatabaseTestBase;
import ee.hm.dop.model.User;

public class UserDAOTest extends DatabaseTestBase{

    @Inject
    private UserDAO userDAO;

    @Test
    public void findUserByIdCode() {
        List<String> ids = new ArrayList<>();
        ids.add("39011220011");
        ids.add("38011550077");
        ids.add("37066990099");

        for (String id : ids) {
            System.out.println(id);
            assertValidUser(userDAO.findUserByIdCode(id));
        }
    }

    private void assertValidUser(User user) {
        assertNotNull(user.getId());

        if(user.getId() == 1) {
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
