package ee.hm.dop.service;

import ee.hm.dop.config.Configuration;
import org.simplejavamail.MailException;
import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static ee.hm.dop.utils.ConfigurationProperties.*;

@Service
public class MailSender {

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    @Inject
    private Configuration configuration;

    public boolean sendEmail(Email email) {
        try {
            MailerBuilder
                    .withSMTPServer(configuration.getString(EMAIL_HOST), configuration.getInt(EMAIL_PORT), configuration.getString(EMAIL_USERNAME), configuration.getString(EMAIL_PASSWORD))
                    .withTransportStrategy(TransportStrategy.valueOf(configuration.getString(EMAIL_TRANSPORT_STRATEGY)))
                    .buildMailer()
                    .sendMail(email);
            return true;
        } catch (MailException e) {
            logger.error("Failed to send e-mail: {}", e.getMessage(), e);
            return false;
        }
    }
}
