package ee.hm.dop.service;

import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.dao.UserDAO;
import ee.hm.dop.exceptions.DuplicateUserException;
import ee.hm.dop.model.User;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserDAO userDAO;

    public User getUserByIdCode(String idCode) {
        return userDAO.findUserByIdCode(idCode);
    }

    public User getUserByUsername(String username) {
        return userDAO.findUserByUsername(username);
    }

    public User create(String idCode, String name, String surname) {
        User user = new User();
        user.setIdCode(idCode);
        user.setName(WordUtils.capitalizeFully(name, ' ', '-'));
        user.setSurname(WordUtils.capitalizeFully(surname, ' ', '-'));

        return create(user);
    }

    public synchronized User create(User user) {
        User newUser = null;
        String generatedUsername = generateUsername(user.getName(), user.getSurname());
        user.setUsername(generatedUsername);

        try {
            newUser = userDAO.update(user);
        } catch (DuplicateUserException e) {
            logger.error(e.getMessage());
        }

        return newUser;
    }

    protected String generateUsername(String name, String surname) {
        Long count = userDAO.countUsersWithSameFullName(name, surname);
        String username = name.toLowerCase() + "." + surname.toLowerCase();
        if (count == 0) {
            return username;
        }
        return username + String.valueOf(count + 1);
    }
}
