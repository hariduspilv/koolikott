package ee.hm.dop.service.login;

import ee.hm.dop.model.AuthenticatedUser;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Minutes;

public class SessionUtil {

    public static final int SESSION_TIME_MIN = 2 * 60;

    public static boolean sessionInValid(AuthenticatedUser authenticatedUser) {
        return !sessionValid(authenticatedUser);
    }

    public static boolean sessionValid(AuthenticatedUser authenticatedUser) {
        return maxTime(authenticatedUser).isAfterNow();
    }

    public static int minRemaining(AuthenticatedUser authenticatedUser) {
        return Minutes.minutesBetween(new Instant(), maxTime(authenticatedUser).toInstant()).getMinutes();
    }

    private static DateTime maxTime(AuthenticatedUser authenticatedUser) {
        return authenticatedUser.getLoginDate().plusMinutes(SESSION_TIME_MIN);
    }
}
