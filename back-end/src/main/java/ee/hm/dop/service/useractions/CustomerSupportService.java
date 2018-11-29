package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.CustomerSupportDao;
import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.User;
import ee.hm.dop.service.SendMailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class CustomerSupportService {

    @Inject
    private CustomerSupportDao customerSupportDao;

    @Inject
    private SendMailService sendMailService;

    public CustomerSupport save(CustomerSupport customerSupport, User user) {

        validateName(customerSupport);
        validateEmail(customerSupport);

        if (isBlank(customerSupport.getSubject()))
            throw new WebApplicationException("Subject is empty", Response.Status.BAD_REQUEST);

        if (isBlank(customerSupport.getMessage()))
            throw new WebApplicationException("Message is empty", Response.Status.BAD_REQUEST);


        customerSupport.setCreatedAt(DateTime.now());
        customerSupport.setUser(user);
        customerSupport.setSentTries(0);

        if (sendMailService.sendEmail(sendMailService.composeEmailToUser(customerSupport))) {
            sendMailService.sendEmail(sendMailService.composeEmailToSupport(customerSupport));
            customerSupport.setSentAt(DateTime.now());
            customerSupport.setSentSuccessfully(true);
            customerSupport.setSentTries(1);
            customerSupport.setErrorMessage("Sent successfully");
        } else {
            customerSupport.setSentTries(2);
            customerSupport.setSentAt(DateTime.now());
            customerSupport.setErrorMessage("Failed to send mail to user");
            if (!sendMailService.sendEmail(sendMailService.composeEmailToSupportWhenSendFailed(customerSupport))) {
                customerSupport.setErrorMessage("Failed to send email to HITSA support");
                customerSupport.setSentTries(3);
            }

        }

        return customerSupportDao.createOrUpdate(customerSupport);
    }

    public void validateName(CustomerSupport customerSupport) {
        if (isNotBlank(customerSupport.getName())) {
            customerSupport.setName(StringUtils.normalizeSpace(customerSupport.getName()));
        } else {
            throw new WebApplicationException("Name is missing", Response.Status.BAD_REQUEST);
        }
    }

    public void validateEmail(CustomerSupport customerSupport) {
        if (isNotBlank(customerSupport.getEmail())) {
            customerSupport.setEmail(StringUtils.trim(customerSupport.getEmail()));
            if (!EmailValidator.getInstance().isValid(customerSupport.getEmail()))
                throw new WebApplicationException("Invalid email address", Response.Status.BAD_REQUEST);
        } else {
            throw new WebApplicationException("Email is empty", Response.Status.BAD_REQUEST);
        }
    }
}
