package ee.hm.dop.utils;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.ReducedLearningObject;
import ee.hm.dop.model.ReducedPortfolio;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;

public class UserUtil {

    public static final String MUST_BE_ADMIN = "Logged in user must be admin.";
    public static final String MUST_BE_ADMIN_OR_MODERATOR = "Logged in user must be admin or moderator.";

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

    public static boolean isUserPublisher(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getPublisher() != null;
    }

    public static boolean isUserAdminOrModerator(User loggedInUser) {
        return isUserAdmin(loggedInUser) || isUserModerator(loggedInUser);
    }

    public static void mustBeModeratorOrAdmin(User loggedInUser) {
        if (!isUserAdminOrModerator(loggedInUser)) {
            throw new RuntimeException(MUST_BE_ADMIN_OR_MODERATOR);
        }
    }

    public static void mustBeAdmin(User loggedInUser) {
        if (!isUserAdmin(loggedInUser)) {
            throw new RuntimeException(MUST_BE_ADMIN);
        }
    }
}
