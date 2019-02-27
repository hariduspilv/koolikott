package ee.hm.dop.service.login;

import ee.hm.dop.model.AuthenticatedUser;

import java.time.Duration;
import java.time.LocalDateTime;

public class SessionUtil {

    public static boolean sessionInValid(AuthenticatedUser authenticatedUser) {
        return !sessionValid(authenticatedUser);
    }

    public static boolean sessionValid(AuthenticatedUser authenticatedUser) {
        return authenticatedUser.getSessionTime().isAfter(LocalDateTime.now());
    }

    public static int minRemaining(AuthenticatedUser authenticatedUser) {
        return (int) (Duration.between(LocalDateTime.now(), authenticatedUser.getSessionTime()).getSeconds() / 60);
    }
}
