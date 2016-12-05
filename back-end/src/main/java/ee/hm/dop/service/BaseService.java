package ee.hm.dop.service;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.User;

/**
 * Created by mart on 23.11.16.
 */
public class BaseService {

    protected boolean isUserCreator(LearningObject learningObject, User loggedInUser) {
        return loggedInUser != null && learningObject.getCreator().getId().equals(loggedInUser.getId());
    }

    protected boolean isUserAdmin(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.ADMIN;
    }

    protected boolean isUserModerator(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.MODERATOR;
    }

    protected boolean isUserAdminOrModerator(User loggedInUser) {
        return isUserAdmin(loggedInUser) || isUserModerator(loggedInUser);
    }
}
