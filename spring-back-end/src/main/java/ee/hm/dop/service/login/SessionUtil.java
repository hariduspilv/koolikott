package ee.hm.dop.service.login;

import ee.hm.dop.model.AuthenticatedUser;
import java.time.LocalDateTime;
import org.joda.time.Instant;
import org.joda.time.Minutes;

import java.time.LocalDateTime;

public class SessionUtil {

    public static boolean sessionInValid(AuthenticatedUser authenticatedUser) {
        return !sessionValid(authenticatedUser);
    }

    public static boolean sessionValid(AuthenticatedUser authenticatedUser) {
        return authenticatedUser.getSessionTime().isAfter(LocalDateTime.now());
    }

    public static int minRemaining(AuthenticatedUser authenticatedUser) {
        return 12;
        //todo
//        return Minutes.minutesBetween(new Instant(), authenticatedUser.getSessionTime().toInstant()).getMinutes();
    }
}
