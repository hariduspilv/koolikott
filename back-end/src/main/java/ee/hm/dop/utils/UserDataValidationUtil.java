package ee.hm.dop.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class UserDataValidationUtil {

    public static String validateEmail (String email) {
        if (isNotBlank(email)) {
            email = StringUtils.trim(email);
            if (EmailValidator.getInstance().isValid(email)) {
                return email;
            }
            throw badRequest("Invalid email address");
        }
        throw badRequest("Email is empty");
    }

    private static WebApplicationException badRequest(String s) {
        return new WebApplicationException(s, Response.Status.BAD_REQUEST);
    }

}
