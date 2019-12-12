package ee.hm.dop.service.login;

import ee.hm.dop.dao.*;
import ee.hm.dop.model.*;
import ee.hm.dop.model.administration.DopPage;
import ee.hm.dop.model.administration.PageableQuerySentEmails;
import ee.hm.dop.service.MailBuilder;
import ee.hm.dop.service.PinGeneratorService;
import ee.hm.dop.service.MailSender;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

import static ee.hm.dop.utils.UserDataValidationUtil.validateEmail;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@Transactional
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
    private MailSender mailSender;
    @Inject
    private MailBuilder mailBuilder;
    @Inject
    private EmailToCreatorDao emailToCreatorDao;
    @Inject
    private LearningObjectDao learningObjectDao;

    public UserEmail getUserEmail(long id) {
        User user = userDao.findUserById(id);
        UserEmail userEmail = userEmailDao.findByUser(user);

        if (user != null && userEmail != null && userEmail.isActivated())
            return userEmail;
         else
            throw badRequest("User or user email not found or e-mail not activated");
    }

    public EmailToCreator sendEmailForCreator(EmailToCreator emailToCreator, User userSender) {
        if (isBlank(emailToCreator.getMessage())) throw badRequest("Message is empty");
        if(emailToCreator.getMessage().length() > 1000){
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
        emailToCreator.setSender(userSender);

        if (mailSender.sendEmail(mailBuilder.sendEmailToCreator(emailToCreator))) {
            emailToCreator.setSentAt(LocalDateTime.now());
            emailToCreator.setSentSuccessfully(true);
            emailToCreator.setSentTries(1);
            emailToCreator.setErrorMessage("Sent successfully");

        } else {
            emailToCreator.setSentTries(2);
            emailToCreator.setSentAt(LocalDateTime.now());
            emailToCreator.setErrorMessage("Failed to send email to creator");
            if (!mailSender.sendEmail(mailBuilder.sendEmailToSupportWhenSendEmailToCreatorFailed(emailToCreator))) {
                emailToCreator.setErrorMessage("Failed to send email to creator");
                emailToCreator.setSentTries(3);
            }
        }
        return emailToCreatorDao.createOrUpdate(emailToCreator);
    }

    public String getEmail(User user) {
        if (user == null)
            throw badRequest("User is null, can't find e-mail of null");

        if (userEmailDao.findByUser(user) != null) {
            if (userEmailDao.findByUser(user).getEmail() == null)
                throw badRequest("Useremail is null, can't find e-mail");
            else
                return userEmailDao.findByUser(user).getEmail();
        } else {
            throw badRequest("Useremail is null, can't find e-mail");
        }
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
        UserEmail dbUserEmail = validateAndActivateEmail(userEmail);
        agreeToAgreement(userAgreementDao.getLatestGdprTermsAgreementForUser(userEmail.getUser().getId()));
        agreeToAgreement(userAgreementDao.getLatestTermsAgreementForUser(userEmail.getUser().getId()));

        return userEmailDao.createOrUpdate(dbUserEmail);
    }

    private void agreeToAgreement(User_Agreement userAgreement) {
        if (userAgreement != null) {
            userAgreement.setAgreed(true);
            userAgreementDao.createOrUpdate(userAgreement);
        }
    }

    public UserEmail validatePinFromProfile(UserEmail userEmail) {
        UserEmail dbUserEmail = validateAndActivateEmail(userEmail);
        return userEmailDao.createOrUpdate(dbUserEmail);
    }

    private UserEmail validateAndActivateEmail(UserEmail userEmail) {
        UserEmail dbUserEmail = userEmailDao.findByUser(userEmail.getUser());
        if (dbUserEmail == null)
            throw badRequest("User not found");
        if (!dbUserEmail.getPin().equals(userEmail.getPin()))
            throw badRequest("Pins not equal");

        dbUserEmail.setActivated(true);
        dbUserEmail.setActivatedAt(LocalDateTime.now());
        dbUserEmail.setEmail(userEmail.getEmail());
        return dbUserEmail;
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
        mailSender.sendEmail(mailBuilder.sendPinToUser(userEmail, email));
        userEmail.setEmail("");
        return userEmail;
    }

    private UserEmail setUserAndSendMail(UserEmail userEmail) {
        validateEmail(userEmail.getEmail());
        userEmail.setActivated(false);
        userEmail.setActivatedAt(null);
        userEmail.setCreatedAt(LocalDateTime.now());
        userEmail.setPin(PinGeneratorService.generatePin());
        mailSender.sendEmail(mailBuilder.sendPinToUser(userEmail));
        userEmail.setEmail("");
        return userEmail;
    }

    private void verifyLOCreator(EmailToCreator emailToCreator) {
        LearningObject learningObject = learningObjectDao.findById(emailToCreator.getLearningObject().getId());
        User creator = learningObject.getCreator();
        if (!creator.getId().equals(emailToCreator.getCreatorId())) {
            throw forbidden("This creator is not creator of this LO");
        }
    }

    private ResponseStatusException forbidden(String s) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, s);
    }

    private ResponseStatusException badRequest(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }

    public DopPage getUserEmail(User loggedInUser, PageableQuerySentEmails pageableQuery) {
        List<EmailToCreator> emails = emailToCreatorDao.getSenderSentEmails(loggedInUser, pageableQuery);
        Long sentEmailsCount = emailToCreatorDao.getSenderSentEmailCount(loggedInUser, pageableQuery);

        DopPage page = new DopPage();
        page.setPage(pageableQuery.getPage());
        page.setSize(pageableQuery.getSize());
        page.setContent(emails);
        page.setTotalElements(sentEmailsCount);
        page.setTotalPages((int) (sentEmailsCount / pageableQuery.getSize()));
        return page;
    }

    public Long getSentEmailsCount(User loggedInUser) {
        return emailToCreatorDao.getSenderSentEmailsCount(loggedInUser);
    }
}
