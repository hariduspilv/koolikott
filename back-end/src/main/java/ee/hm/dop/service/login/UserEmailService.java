package ee.hm.dop.service.login;

import ee.hm.dop.dao.UserAgreementDao;
import ee.hm.dop.dao.UserEmailDao;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.model.User_Agreement;
import ee.hm.dop.service.PinGeneratorService;
import ee.hm.dop.service.SendMailService;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static ee.hm.dop.utils.UserDataValidationUtil.validateEmail;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class UserEmailService {

    @Inject
    private UserEmailDao userEmailDao;

    @Inject
    UserAgreementDao userAgreementDao;

    @Inject
    private SendMailService sendMailService;

    public UserEmail save(UserEmail userEmail) {
        UserEmail dbUserEmail = userEmailDao.findByField("user", userEmail.getUser());
        if (dbUserEmail != null && dbUserEmail.getUser().getId() == userEmail.getUser().getId()) {
            dbUserEmail.setEmail(userEmail.getEmail());
            setUserAndSendMail(dbUserEmail);
            return userEmailDao.createOrUpdate(dbUserEmail);
        } else {
            if (hasDuplicateEmail(userEmail))
                throw badRequest("Duplicate email in database");

            if (userEmail.getUser() == null)
                throw badRequest("User is null");
            userEmail.setEmail(validateEmail(userEmail.getEmail()));
            setUserAndSendMail(userEmail);
            return userEmailDao.createOrUpdate(userEmail);
        }
    }

    public boolean hasDuplicateEmail(UserEmail userEmail) {
        if (isBlank(userEmail.getEmail()))
            throw badRequest("Email Empty");
        UserEmail dbUserEmail = userEmailDao.findByField("email", userEmail.getEmail());

        if (dbUserEmail == null)
            return false;
        else
            return true;
    }

    public UserEmail validatePin(UserEmail userEmail) {
        UserEmail dbUserEmail = userEmailDao.findByField("user", userEmail.getUser());
        User_Agreement dbUserAgreement = userAgreementDao.getLatestAgreementForUser(userEmail.getUser().getId());
        if (dbUserEmail != null) {
            if (dbUserEmail.getPin().equals(userEmail.getPin())) {
                dbUserEmail.setActivated(true);
                dbUserEmail.setActivatedAt(DateTime.now());
                dbUserAgreement.setAgreed(true);
                userAgreementDao.createOrUpdate(dbUserAgreement);
            } else {
                throw badRequest("Pins not equal");
            }
        } else {
            throw notFound("User not found");
        }

        return userEmailDao.createOrUpdate(dbUserEmail);
    }

    private void setUserAndSendMail(UserEmail userEmail) {
        userEmail.setActivated(false);
        userEmail.setActivatedAt(null);
        userEmail.setCreatedAt(DateTime.now());
        userEmail.setPin(PinGeneratorService.generatePin());
        sendMailService.sendEmail(sendMailService.sendPinToUser(userEmail));
    }

    private WebApplicationException badRequest(String s) {
        return new WebApplicationException(s, Response.Status.BAD_REQUEST);
    }

    private WebApplicationException notFound(String s) {
        return new WebApplicationException(s, Response.Status.NOT_FOUND);
    }
}
