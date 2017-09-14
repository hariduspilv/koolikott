package ee.hm.dop.utils;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.ReducedPortfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;

public class UserUtil {
    public static boolean isUserCreator(LearningObject learningObject, User loggedInUser) {
        return loggedInUser != null && learningObject.getCreator().getId().equals(loggedInUser.getId());
    }

    public static boolean isUserCreator(ReducedLearningObject reducedLearningObject, User user) {
        return user != null && reducedLearningObject.getCreator().getId().equals(user.getId());
    }

    public static boolean isUserAdmin(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.ADMIN;
    }

    public static boolean isUserModerator(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.MODERATOR;
    }

    public static boolean isUserAdminOrModerator(User loggedInUser) {
        return isUserAdmin(loggedInUser) || isUserModerator(loggedInUser);
    }
}
