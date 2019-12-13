package ee.hm.dop.utils;

import ee.hm.dop.model.Media;
import ee.hm.dop.model.User;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.interfaces.ILearningObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserUtil {

    public static final String MUST_BE_ADMIN = "Logged in user must be admin.";
    public static final String MUST_BE_MODERATOR = "Logged in user must be moderator.";
    public static final String MUST_BE_ADMIN_OR_MODERATOR = "Logged in user must be admin or moderator.";
    public static final String MUST_BE_CREATOR = "Logged in user must be creator";

    public static boolean isCreator(ILearningObject learningObject, User loggedInUser) {
        return loggedInUser != null && learningObject.getCreator().getId().equals(loggedInUser.getId());
    }

    public static boolean isCreator(Media media, User loggedInUser) {
        return loggedInUser != null && media.getCreatedBy().getId().equals(loggedInUser.getId());
    }

    public static boolean isAdmin(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.ADMIN;
    }

    public static boolean isModerator(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getRole() == Role.MODERATOR;
    }

    public static boolean isPublisher(User loggedInUser) {
        return loggedInUser != null && loggedInUser.getPublisher() != null;
    }

    public static boolean isAdminOrModerator(User loggedInUser) {
        return isAdmin(loggedInUser) || isModerator(loggedInUser);
    }

    public static void mustBeModeratorOrAdmin(User loggedInUser) {
        if (!isAdminOrModerator(loggedInUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MUST_BE_ADMIN_OR_MODERATOR);
        }
    }

    public static void mustBeAdmin(User loggedInUser) {
        if (!isAdmin(loggedInUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MUST_BE_ADMIN);
        }
    }

    public static void mustBeModerator(User loggedInUser) {
        if (!isModerator(loggedInUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MUST_BE_MODERATOR);
        }
    }

    public static void mustBeCreator(ILearningObject learningObject, User loggedInUser) {
        if (!isCreator(learningObject, loggedInUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MUST_BE_CREATOR);
        }
    }
}
