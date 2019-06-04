package ee.hm.dop.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class UserDataValidationUtil {

    public static String validateEmail(String email) {
        if (isBlank(email)) {
            throw badRequest("Email is empty");
        }
        String trimmedEmail = StringUtils.trim(email);
        if (!EmailValidator.getInstance().isValid(trimmedEmail)) {
            throw badRequest("Invalid email address");
        }
        return trimmedEmail;
    }

    private static ResponseStatusException badRequest(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }

}