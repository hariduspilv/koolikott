package ee.hm.dop.service;

import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.utils.DateUtils;
import org.apache.commons.configuration2.Configuration;
import org.simplejavamail.MailException;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static ee.hm.dop.utils.ConfigurationProperties.*;

public class SendMailService {

    @Inject
    private Configuration configuration;

    private static final Logger logger = LoggerFactory.getLogger(SendMailService.class);
    private static final String BREAK = System.getProperty("line.separator");

    public Email composeEmailToUser(CustomerSupport customerSupport) {
        return EmailBuilder.startingBlank()
                .from("HITSA Support", customerSupport.getEmail())
                .to("Support", customerSupport.getEmail())
                .withSubject("e-Koolikott, küsimuse kinnitus")
                .withPlainText("Teema: " + customerSupport.getSubject() + BREAK +
                        "Küsimus: " + customerSupport.getMessage())
                .buildEmail();
    }

    public Email composeEmailToSupport(CustomerSupport customerSupport) {
        return EmailBuilder.startingBlank()
                .from("e-Koolikott", customerSupport.getEmail())
                .to("HITSA Support", configuration.getString(EMAIL_ADDRESS))
                .withSubject("e-Koolikott: " + customerSupport.getSubject())
                .withPlainText("Küsimus: " + customerSupport.getMessage() + BREAK +
                        "Küsija kontakt: " + customerSupport.getName() + " " + customerSupport.getEmail())
                .buildEmail();
    }

    public Email composeEmailToSupportWhenSendFailed(CustomerSupport customerSupport) {
        return EmailBuilder.startingBlank()
                .from("e-Koolikott", customerSupport.getEmail())
                .to("HITSA Support", configuration.getString(EMAIL_ADDRESS))
                .withSubject("Kasutaja ebaõnnestunud pöördumine")
                .withPlainText("Kasutaja: " + customerSupport.getName() +", " + customerSupport.getEmail() + BREAK
                + "On saatnud pöördumise teemaga: " + customerSupport.getSubject() + BREAK
                + "Sisuga: " + customerSupport.getMessage() + BREAK
                + "Pöördumine saadeti: " + DateUtils.toStringWithoutMillis(customerSupport.getSentAt()))
                .buildEmail();
    }


    public boolean sendEmail(Email email) {
        try {
            MailerBuilder
                    .withSMTPServer(configuration.getString(EMAIL_HOST), configuration.getInt(EMAIL_PORT), configuration.getString(EMAIL_USERNAME), configuration.getString(EMAIL_PASSWORD))
                    .withTransportStrategy(TransportStrategy.valueOf(configuration.getString(EMAIL_TRANSPORT_STRATEGY)))
                    .buildMailer()
                    .sendMail(email);

            return true;

        } catch (MailException e) {
            logger.info("Failed to send e-mail", e);
            return false;
        }
    }
}
