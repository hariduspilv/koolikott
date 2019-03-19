package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.*;
import ee.hm.dop.model.*;
import ee.hm.dop.service.PinGeneratorService;
import ee.hm.dop.service.SendMailService;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.HttpURLConnection;
import java.util.Arrays;

import static ee.hm.dop.utils.UserDataValidationUtil.validateEmail;

public class UserProfileService {


    @Inject
    private UserProfileDao userProfileDao;
    @Inject
    private UserDao userDao;
    @Inject
    private UserEmailDao userEmailDao;
    @Inject
    private SendMailService sendMailService;

    public static final String[] ROLES = {"TEACHER", "STUDENT", "PARENT", "OTHER"};

    public Response update(UserProfile userProfile, User user) {
        Response response = Response.status(HttpURLConnection.HTTP_OK).build();
        if (!userProfile.getRole().isEmpty() && !Arrays.stream(ROLES).anyMatch(userProfile.getRole()::equals))
            throw badRequest("Role missing or invalid");

        updateUser(userProfile, user);
        UserEmail dbUserEmail = userEmailDao.findByUser(user);
        if (dbUserEmail != null && !dbUserEmail.getEmail().equals(validateEmail(userProfile.getEmail()))) {
            dbUserEmail.setPin(PinGeneratorService.generatePin());
            sendMailService.sendEmail(sendMailService.sendPinToUser(dbUserEmail, userProfile.getEmail()));
            userEmailDao.createOrUpdate(dbUserEmail);
            response = Response.status(HttpURLConnection.HTTP_CREATED).build();
        } else if (dbUserEmail == null ){
            createUserEmail(user);
            response = Response.status(HttpURLConnection.HTTP_CREATED).build();
        }

        UserProfile dbUserProfile = userProfileDao.findByUser(user);
        if (dbUserProfile != null) {
            dbUserProfile.setLastUpdate(DateTime.now());
            dbUserProfile.setRole(userProfile.getRole());
            userProfileDao.createOrUpdate(dbUserProfile);
            return response;
        }
        userProfile.setLastUpdate(DateTime.now());
        userProfileDao.createOrUpdate(userProfile);
        return response;

    }

    public UserProfile getUserProfile(User loggedInUser) {
        User user = userDao.findUserById(loggedInUser.getId());
        UserProfile userProfile = userProfileDao.findByUser(user);
        if (userProfile == null)
            return null;

        userProfile.setInstitutions(user.getInstitutions());
        userProfile.setTaxons(user.getTaxons());
        return userProfile;
    }

    private void createUserEmail(User user) {
        UserEmail userEmail = new UserEmail();
        userEmail.setPin(PinGeneratorService.generatePin());
        userEmail.setUser(user);
        userEmail.setActivated(false);
        userEmail.setActivatedAt(null);
        userEmail.setCreatedAt(DateTime.now());
        userEmail.setEmail("");
        if (sendMailService.sendEmail(sendMailService.sendPinToUser(userEmail)));
            userEmailDao.createOrUpdate(userEmail);
    }

    private void updateUser(UserProfile userProfile, User user) {
        User dbUser = userDao.findUserById(user.getId());
        if (dbUser != null) {
            if (userProfile.getRole().equals("PARENT") || userProfile.getRole().equals("OTHER")) {
                dbUser.setInstitutions(null);
            } else {
                dbUser.setInstitutions(userProfile.getInstitutions());
                dbUser.setTaxons(userProfile.getTaxons());
            }
            userDao.createOrUpdate(dbUser);
        } else {
            throw badRequest("User not found");
        }
    }

    private WebApplicationException badRequest(String s) {
        return new WebApplicationException(s, Response.Status.BAD_REQUEST);
    }
}
