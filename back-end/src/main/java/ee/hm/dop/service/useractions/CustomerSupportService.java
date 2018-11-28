package ee.hm.dop.service.useractions;

import ee.hm.dop.dao.CustomerSupportDao;
import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.User;
import ee.hm.dop.service.SendMailService;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class CustomerSupportService {

    @Inject
    private CustomerSupportDao customerSupportDao;

    @Inject
    private SendMailService sendMailService;

    public CustomerSupport save(CustomerSupport customerSupport, User user) {

        customerSupport.setName(customerSupport.getName());
        customerSupport.setEmail(customerSupport.getEmail());
        customerSupport.setSubject(customerSupport.getSubject());
        customerSupport.setMessage(customerSupport.getMessage());
        customerSupport.setCreatedAt(DateTime.now());
        customerSupport.setUser(user);
        customerSupport.setSentTries(0);

        if (sendMailService.sendEmail(sendMailService.composeEmailToUser(customerSupport))) {
            sendMailService.sendEmail(sendMailService.composeEmailToSupport(customerSupport));
            customerSupport.setSentAt(DateTime.now());
            customerSupport.setSentSuccessfully(true);
            customerSupport.setSentTries(1);
        } else {
            customerSupport.setSentAt(DateTime.now());
            customerSupport.setErrorMessage("Failed to send mail to user");
            sendMailService.sendEmail(sendMailService.composeEmailToSupportWhenSendFailed(customerSupport));

        }

        return customerSupportDao.createOrUpdate(customerSupport);
    }
}
