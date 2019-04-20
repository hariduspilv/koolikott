package ee.hm.dop.service.login;

import ee.hm.dop.dao.*;
import ee.hm.dop.model.*;
import ee.hm.dop.model.administration.PageableQuery;
import ee.hm.dop.service.PinGeneratorService;
import ee.hm.dop.service.SendMailService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

import static ee.hm.dop.utils.UserDataValidationUtil.validateEmail;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
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
        if(emailToCreator.getMessage().length() > 500){
            throw badRequest("Message is too long");
        }
        verifyLOCreator(emailToCreator);

        UserEmail userSenderEmail = userEmailDao.findByUser(userSender);
        User userCreator = userDao.findUserById(emailToCreator.getCreatorId());
        UserEmail creatorEmail = userEmailDao.findByUser(userCreator);

        emailToCreator.setCreatorEmail(creatorEmail.getEmail());
        emailToCreator.setSenderName(userSender.getFullName());
        emailToCreator.setSenderEmail(userSenderEmail.getEmail());
        emailToCreator.setUser(userCreator);
        emailToCreator.setCreatedAt(LocalDateTime.now());
        emailToCreator.setSentTries(0);

        if (sendMailService.sendEmail(sendMailService.sendEmailToCreator(emailToCreator))) {
            sendMailService.sendEmail(sendMailService.sendEmailToExpertSelf(emailToCreator));
            emailToCreator.setSentAt(LocalDateTime.now());
            emailToCreator.setSentSuccessfully(true);
            emailToCreator.setSentTries(1);
            emailToCreator.setErrorMessage("Sent successfully");

        } else {
            emailToCreator.setSentTries(2);
            emailToCreator.setSentAt(LocalDateTime.now());
            emailToCreator.setErrorMessage("Failed to send email to creator");
            if (!sendMailService.sendEmail(sendMailService.sendEmailToSupportWhenSendEmailToCreatorFailed(emailToCreator))) {
                emailToCreator.setErrorMessage("Failed to send email to creator");
                emailToCreator.setSentTries(3);
            }
        }
        return emailToCreatorDao.createOrUpdate(emailToCreator);
    }

    public String getEmail(User user) {
        if (user == null)
            throw badRequest("User is null, can't find e-mail of null");

        return userEmailDao.findByUser(user).getEmail();

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
        dbUserEmail.setActivatedAt(LocalDateTime.now());
        dbUserEmail.setEmail(userEmail.getEmail());
        dbUserAgreement.setAgreed(true);
        userAgreementDao.createOrUpdate(dbUserAgreement);


        return userEmailDao.createOrUpdate(dbUserEmail);
    }

    public UserEmail validatePinFromProfile(UserEmail userEmail) {
        UserEmail dbUserEmail = userEmailDao.findByUser(userEmail.getUser());
        if (dbUserEmail == null)
            throw notFound("User not found");
        if (!dbUserEmail.getPin().equals(userEmail.getPin()))
            throw badRequest("Pins not equal");

        dbUserEmail.setActivated(true);
        dbUserEmail.setActivatedAt(LocalDateTime.now());
        dbUserEmail.setEmail(userEmail.getEmail());

        return userEmailDao.createOrUpdate(dbUserEmail);
    }

    public boolean hasDuplicateEmailForProfile(UserEmail userEmail, User loggedInUser) {
        if (isBlank(userEmail.getEmail()))
            throw badRequest("Email Empty");
        User user = userDao.findUserById(loggedInUser.getId());
        UserEmail dbUserEmail = userEmailDao.findByEmail(userEmail.getEmail());
        return dbUserEmail != null && !user.equals(dbUserEmail.getUser());
    }

    public Boolean hasEmail(UserEmail userEmail) {
        AuthenticationState state = authenticationStateDao.findAuthenticationStateByToken(userEmail.getUserStatus().getToken());
        User user = userDao.findUserByIdCode(state.getIdCode());
        UserEmail dbUserEmail = userEmailDao.findByUser(user);
        return dbUserEmail != null && !isEmpty(dbUserEmail.getEmail());
    }

    private UserEmail setUserAndSendMail(UserEmail userEmail, UserEmail email) {
        validateEmail(email.getEmail());
        userEmail.setActivated(false);
        userEmail.setActivatedAt(null);
        userEmail.setCreatedAt(LocalDateTime.now());
        userEmail.setPin(PinGeneratorService.generatePin());
        sendMailService.sendEmail(sendMailService.sendPinToUser(userEmail, email));
        userEmail.setEmail("");
        return userEmail;
    }

    private UserEmail setUserAndSendMail(UserEmail userEmail) {
        validateEmail(userEmail.getEmail());
        userEmail.setActivated(false);
        userEmail.setActivatedAt(null);
        userEmail.setCreatedAt(LocalDateTime.now());
        userEmail.setPin(PinGeneratorService.generatePin());
        sendMailService.sendEmail(sendMailService.sendPinToUser(userEmail));
        userEmail.setEmail("");
        return userEmail;
    }

    private void verifyLOCreator(EmailToCreator emailToCreator) {
        LearningObject learningObject = learningObjectDao.findById(emailToCreator.getLearningObjectId());
        User creator = learningObject.getCreator();
        if (!creator.getId().equals(emailToCreator.getCreatorId())) {
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

    public SearchResult getUserEmail(User loggedInUser, PageableQuery pageableQuery) {

        List<EmailToCreator> emails = emailToCreatorDao.getSenderSentEmails(loggedInUser, pageableQuery);
        Long sentEmailsCount = emailToCreatorDao.getSenderSentEmailCount(loggedInUser, pageableQuery);

        SearchResult searchResult = new SearchResult();
        searchResult.setItems(emails);
        searchResult.setTotalResults(sentEmailsCount);
        return searchResult;
    }

    public Long getSentEmailsCount(User loggedInUser) {
        return emailToCreatorDao.getSenderSentEmailsCount(loggedInUser);
    }
}
