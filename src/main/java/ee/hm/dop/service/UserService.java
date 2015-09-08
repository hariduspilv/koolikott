package ee.hm.dop.service;

import static java.lang.String.format;

import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.dao.UserDAO;
import ee.hm.dop.model.Role;
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
        user.setName(name);
        user.setSurname(surname);

        return create(user);
    }

    public synchronized User create(User user) {
        user.setName(WordUtils.capitalizeFully(user.getName(), ' ', '-'));
        user.setSurname(WordUtils.capitalizeFully(user.getSurname(), ' ', '-'));
        String generatedUsername = generateUsername(user.getName(), user.getSurname());
        user.setUsername(generatedUsername);
        user.setRole(Role.USER);

        logger.info(format("Creating user: username = %s; name = %s; surname = %s; idCode = %s", user.getUsername(),
                user.getName(), user.getSurname(), user.getIdCode()));

        return userDAO.update(user);
    }

    protected String generateUsername(String name, String surname) {
        Long count = userDAO.countUsersWithSameFullName(name, surname);
        String username = name.trim().toLowerCase() + "." + surname.trim().toLowerCase();
        username = username.replaceAll("\\s+", ".");

        if (count > 0) {
            username += String.valueOf(count + 1);
        }

        return username;
    }
}
