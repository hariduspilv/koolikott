package ee.hm.dop.service.login;

import ee.hm.dop.dao.AuthenticationStateDao;
import ee.hm.dop.dao.UserAgreementDao;
import ee.hm.dop.dao.UserDao;
import ee.hm.dop.dao.UserEmailDao;
import ee.hm.dop.model.AuthenticationState;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.User_Agreement;
import ee.hm.dop.service.PinGeneratorService;
import ee.hm.dop.service.SendMailService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.inject.Inject;

import java.time.LocalDateTime;

import static ee.hm.dop.utils.UserDataValidationUtil.validateEmail;
import static org.apache.commons.lang.StringUtils.isBlank;

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
    private SendMailService sendMailService;

    public UserEmail save(UserEmail userEmail) {
        if (userEmail.getUser() == null) {
            throw badRequest("User is null");
        }

        UserEmail dbUserEmail = userEmailDao.findByUserId(userEmail.getUser());
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
        UserEmail dbUserEmail = userEmailDao.findByUserId(userEmail.getUser());
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

    private ResponseStatusException badRequest(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }

    private ResponseStatusException notFound(String s) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, s);
    }
}