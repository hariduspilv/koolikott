package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.UserDAO;
import ee.hm.dop.model.User;

/**
 * Created by mart.laus on 13.08.2015.
 */
public class UserService {

    @Inject
    private UserDAO userDAO;

    public User getUserByIdCode(String idCode) {
        return userDAO.findUserByIdCode(idCode);
    }

    public User getUserByUsername(String username) {
        return userDAO.findUserByUsername(username);
    }

    public void createUser(User user) {
        userDAO.createUser(user);
    }
}
