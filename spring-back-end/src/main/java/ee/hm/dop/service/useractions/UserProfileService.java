package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.UserEmailDao;
import ee.hm.dop.dao.UserProfileDao;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.UserProfile;
import ee.hm.dop.service.PinGeneratorService;
import ee.hm.dop.service.SendMailService;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import java.time.LocalDateTime;

import static ee.hm.dop.utils.UserDataValidationUtil.validateEmail;

@Service
public class UserProfileService {


    @Inject
    private UserProfileDao userProfileDao;
    @Inject
    private UserDao userDao;
    @Inject
    private UserEmailDao userEmailDao;
    @Inject
    private SendMailService sendMailService;

    public ResponseEntity<?> update(UserProfile userProfile, User user) {
        ResponseEntity<?> response = ResponseEntity.status(HttpStatus.OK).build();

        updateUser(userProfile, user);
        UserEmail dbUserEmail = userEmailDao.findByUser(user);
        if (dbUserEmail != null && !dbUserEmail.getEmail().equals(validateEmail(userProfile.getEmail()))) {
            dbUserEmail.setPin(PinGeneratorService.generatePin());
            sendMailService.sendEmail(sendMailService.sendPinToUser(dbUserEmail, userProfile.getEmail()));
            userEmailDao.createOrUpdate(dbUserEmail);
            response = ResponseEntity.status(HttpStatus.CREATED).build();
        } else if (dbUserEmail == null ){
            createUserEmail(user);
            response = ResponseEntity.status(HttpStatus.CREATED).build();
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
        userEmail.setCreatedAt(LocalDateTime.now());
        userEmail.setEmail("");
        if (sendMailService.sendEmail(sendMailService.sendPinToUser(userEmail)));
            userEmailDao.createOrUpdate(userEmail);
    }

    private void updateUser(UserProfile userProfile, User user) {
        User dbUser = userDao.findUserById(user.getId());
        if (dbUser != null) {
            if (userProfile.getRole() != null) {
                if (userProfile.getRole().equals("PARENT") || userProfile.getRole().startsWith("OTHER")) {
                    dbUser.setInstitutions(null);
                } else {
                    dbUser.setInstitutions(userProfile.getInstitutions());
                    dbUser.setTaxons(userProfile.getTaxons());
                }
            } else {
                dbUser.setInstitutions(null);
            }
            userDao.createOrUpdate(dbUser);
        } else {
            throw badRequest("User not found");
        }
    }

    private WebApplicationException badRequest(String s) {
        return new WebApplicationException(s, Status.BAD_REQUEST);
    }
}
