package ee.hm.dop.service.login;

import ee.hm.dop.dao.*;
import ee.hm.dop.model.*;
import ee.hm.dop.service.PinGeneratorService;
import ee.hm.dop.service.SendMailService;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.List;

import static ee.hm.dop.utils.UserDataValidationUtil.validateEmail;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class UserEmailService {

    @Inject
    private UserEmailDao userEmailDao;
    @Inject
    private UserAgreementDao userAgreementDao;
    @Inject
    private AuthenticationStateDao authenticationStateDao;
    @Inject
    private UserDao userDao;
    @Inject
    private SendMailService sendMailService;
    @Inject
    private EmailToCreatorDao emailToCreatorDao;
    @Inject
    private LearningObjectDao learningObjectDao;

    public UserEmail getUserEmail(long id) {

        User user = userDao.findUserById(id);
        if (user == null) {
            throw notFound("User not found");
        }
        UserEmail userEmail = userEmailDao.findByUser(user);
        if (userEmail == null) {
            throw notFound("User email not found");
        }
        return userEmail;
    }

    public EmailToCreator sendEmailForCreator(EmailToCreator emailToCreator, User userSender) {

        if (isBlank(emailToCreator.getMessage())) throw badRequest("Message is empty");
        verifyLOCreator(emailToCreator);

        UserEmail userSenderEmail = userEmailDao.findByUser(userSender);
        User userCreator = userDao.findUserById(emailToCreator.getCreatorId());
        UserEmail creatorEmail = userEmailDao.findByUser(userCreator);

        emailToCreator.setCreatorEmail(creatorEmail.getEmail());
        emailToCreator.setSenderName(userSender.getFullName());
        emailToCreator.setSenderEmail(userSenderEmail.getEmail());
        emailToCreator.setUser(userCreator);
        emailToCreator.setCreatedAt(DateTime.now());
        emailToCreator.setSentTries(0);

        if (sendMailService.sendEmail(sendMailService.sendEmailToCreator(emailToCreator))) {
            sendMailService.sendEmail(sendMailService.sendEmailToExpertSelf(emailToCreator));
            emailToCreator.setSentAt(DateTime.now());
            emailToCreator.setSentSuccessfully(true);
            emailToCreator.setSentTries(1);
            emailToCreator.setErrorMessage("Sent successfully");

        } else {
            emailToCreator.setSentTries(2);
            emailToCreator.setSentAt(DateTime.now());
            emailToCreator.setErrorMessage("Failed to send email to creator");
            if (!sendMailService.sendEmail(sendMailService.sendEmailToSupportWhenSendEmailToCreatorFailed(emailToCreator))) {
                emailToCreator.setErrorMessage("Failed to send email to creator");
                emailToCreator.setSentTries(3);
            }
        }
        return emailToCreatorDao.createOrUpdate(emailToCreator);
    }

    public UserEmail save(UserEmail userEmail) {
        UserEmail dbUserEmail = userEmailDao.findByUser(userEmail.getUser());
        if (userEmail.getUser() == null)
            throw badRequest("User is null");

        if (dbUserEmail != null && dbUserEmail.getUser().getId().equals(userEmail.getUser().getId())) {
            return userEmailDao.createOrUpdate(setUserAndSendMail(dbUserEmail, userEmail));
        }
        return userEmailDao.createOrUpdate(setUserAndSendMail(userEmail));
    }

    public boolean hasDuplicateEmail(UserEmail userEmail) {
        if (isBlank(userEmail.getEmail()))
            throw badRequest("Email Empty");
        AuthenticationState state = authenticationStateDao.findAuthenticationStateByToken(userEmail.getUserStatus().getToken());
        User user = userDao.findUserByIdCode(state.getIdCode());
        UserEmail dbUserEmail = userEmailDao.findByEmail(userEmail.getEmail());
        return dbUserEmail != null && !user.equals(dbUserEmail.getUser());
    }

    public UserEmail validatePin(UserEmail userEmail) {
        UserEmail dbUserEmail = userEmailDao.findByUser(userEmail.getUser());
        User_Agreement dbUserAgreement = userAgreementDao.getLatestAgreementForUser(userEmail.getUser().getId());
        if (dbUserEmail == null)
            throw notFound("User not found");
        if (!dbUserEmail.getPin().equals(userEmail.getPin()))
            throw badRequest("Pins not equal");

        dbUserEmail.setActivated(true);
        dbUserEmail.setActivatedAt(DateTime.now());
        dbUserEmail.setEmail(userEmail.getEmail());
        dbUserAgreement.setAgreed(true);
        userAgreementDao.createOrUpdate(dbUserAgreement);


        return userEmailDao.createOrUpdate(dbUserEmail);
    }

    private UserEmail setUserAndSendMail(UserEmail userEmail, UserEmail email) {
        validateEmail(email.getEmail());
        userEmail.setActivated(false);
        userEmail.setActivatedAt(null);
        userEmail.setCreatedAt(DateTime.now());
        userEmail.setPin(PinGeneratorService.generatePin());
        sendMailService.sendEmail(sendMailService.sendPinToUser(userEmail, email));
        userEmail.setEmail("");
        return userEmail;
    }

    private UserEmail setUserAndSendMail(UserEmail userEmail) {
        validateEmail(userEmail.getEmail());
        userEmail.setActivated(false);
        userEmail.setActivatedAt(null);
        userEmail.setCreatedAt(DateTime.now());
        userEmail.setPin(PinGeneratorService.generatePin());
        sendMailService.sendEmail(sendMailService.sendPinToUser(userEmail));
        userEmail.setEmail("");
        return userEmail;
    }

    private void verifyLOCreator(EmailToCreator emailToCreator) {
        LearningObject learningObject = learningObjectDao.findById(emailToCreator.getLearningObjectId());
        List<Long> creatorLearningObjectsIds = learningObjectDao.getAllCreatorLearningObjects(emailToCreator.getCreatorId());
        Long learningObjectId = creatorLearningObjectsIds.stream().filter(lo -> learningObject.getId().equals(lo)).findAny().orElse(null);

        if (learningObjectId == null) {
            throw forbidden("This creator is not creator of this LO");
        }
    }

    private WebApplicationException forbidden(String s) {
        return new WebApplicationException(s, Response.Status.FORBIDDEN);
    }

    private WebApplicationException badRequest(String s) {
        return new WebApplicationException(s, Response.Status.BAD_REQUEST);
    }

    private WebApplicationException notFound(String s) {
        return new WebApplicationException(s, Response.Status.NOT_FOUND);
    }
}
