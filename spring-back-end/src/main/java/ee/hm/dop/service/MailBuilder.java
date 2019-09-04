package ee.hm.dop.service;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.model.AttachedFile;
import ee.hm.dop.model.CustomerSupport;
import ee.hm.dop.model.EmailToCreator;
import ee.hm.dop.model.UserEmail;
import ee.hm.dop.utils.DateUtils;
import org.simplejavamail.email.AttachmentResource;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ee.hm.dop.utils.ConfigurationProperties.EMAIL_ADDRESS;
import static ee.hm.dop.utils.ConfigurationProperties.EMAIL_NO_REPLY_ADDRESS;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

@Service
public class MailBuilder {

    @Inject
    private Configuration configuration;

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private static final String BREAK = "<br/>";

    public Email composeEmailToUser(CustomerSupport customerSupport) {
        return EmailBuilder.startingBlank()
                .from("E-koolikott", configuration.getString(EMAIL_NO_REPLY_ADDRESS))
                .to(customerSupport.getName(), customerSupport.getEmail())
                .withSubject("E-koolikott, küsimuse kinnitus")
                .withHTMLText("<b>Teema:</b> " + customerSupport.getSubject() + BREAK +
                        "<b>Küsimus:</b> " + customerSupport.getMessage())
                .buildEmail();
    }

    public Email composeEmailToSupport(CustomerSupport customerSupport) {
        return EmailBuilder.startingBlank()
                .from("E-koolikott", customerSupport.getEmail())
                .to("HITSA Support", configuration.getString(EMAIL_ADDRESS))
                .withSubject("E-koolikott: " + customerSupport.getSubject())
                .withHTMLText("<b>Küsimus:</b> " + customerSupport.getMessage() + BREAK +
                        "<b>Küsija kontakt:</b> " + customerSupport.getName() + ", " + customerSupport.getEmail())
                .withAttachments(getAttachmentResources(customerSupport))
                .buildEmail();
    }

    private List<AttachmentResource> getAttachmentResources(CustomerSupport customerSupport) {
        if (customerSupport.getFiles() == null) {
            return new ArrayList<>();
        }
        return customerSupport.getFiles().stream()
                .map(a -> decodeAttachment(a))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private AttachmentResource decodeAttachment(AttachedFile a) {
        try {
            return new AttachmentResource(a.getName(), new ByteArrayDataSource(decodeBase64(getContent(a)), "image/*"));
        } catch (Exception e) {
            return null;
        }
    }

    private String getContent(AttachedFile a) {
        return a.getContent().substring(a.getContent().indexOf(',') + 1);
    }

    public Email composeEmailToSupportWhenSendFailed(CustomerSupport customerSupport) {
        return EmailBuilder.startingBlank()
                .from("E-koolikott", customerSupport.getEmail())
                .to("HITSA Support", configuration.getString(EMAIL_ADDRESS))
                .withSubject("Kasutaja ebaõnnestunud pöördumine")
                .withHTMLText("Kasutaja: " + customerSupport.getName() +", " + customerSupport.getEmail() + BREAK
                        + "On saatnud pöördumise teemaga: " + customerSupport.getSubject() + BREAK
                        + "Sisuga: " + customerSupport.getMessage() + BREAK
                        + "Pöördumine saadeti: " + DateUtils.toStringWithoutMillis(customerSupport.getSentAt()))
                .buildEmail();
    }

    public Email sendPinToUser(UserEmail userEmail, UserEmail email) {
        return EmailBuilder.startingBlank()
                .from("E-koolikott", configuration.getString(EMAIL_NO_REPLY_ADDRESS))
                .to(userEmail.getUser().getFullName(), email.getEmail())
                .withSubject("E-koolikotis e-posti kinnitamine")
                .withHTMLText("Tere, " + userEmail.getUser().getFullName()+ BREAK +
                        "Aitäh, et oled E-koolikoti kasutaja!" + BREAK +
                        "Palun trüki allolev kood e-posti aadressi kinnitamise aknasse." + BREAK +
                        BREAK +
                        "<span style=\"font-size: 18px\"><b>" + userEmail.getPin() + "</b></span>" + BREAK + BREAK +
                        "Pane tähele, et kood on personaalne, ära saada seda teistele kasutajatele edasi." + BREAK +
                        "Küsimuste korral kirjuta: e-koolikott@hitsa.ee")
                .buildEmail();
    }

    public Email sendPinToUser(UserEmail userEmail, String email) {
        return EmailBuilder.startingBlank()
                .from("E-koolikott", configuration.getString(EMAIL_NO_REPLY_ADDRESS))
                .to(userEmail.getUser().getFullName(), email)
                .withSubject("E-koolikotis e-posti kinnitamine")
                .withHTMLText("Tere, " + userEmail.getUser().getFullName()+ BREAK +
                        "Aitäh, et oled E-koolikoti kasutaja!" + BREAK +
                        "Palun trüki allolev kood e-posti aadressi kinnitamise aknasse." + BREAK +
                        BREAK +
                        "<span style=\"font-size: 18px\"><b>" + userEmail.getPin() + "</b></span>" + BREAK + BREAK +
                        "Pane tähele, et kood on personaalne, ära saada seda teistele kasutajatele edasi." + BREAK +
                        "Küsimuste korral kirjuta: e-koolikott@hitsa.ee")
                .buildEmail();
    }

    public Email sendPinToUser(UserEmail userEmail) {
        return EmailBuilder.startingBlank()
                .from("E-koolikott", configuration.getString(EMAIL_NO_REPLY_ADDRESS))
                .to(userEmail.getUser().getFullName(), userEmail.getEmail())
                .withSubject("E-koolikotis e-posti kinnitamine")
                .withHTMLText("Tere, " + userEmail.getUser().getFullName()+ BREAK +
                        "Aitäh, et oled E-koolikoti kasutaja!" + BREAK +
                        "Palun trüki allolev kood e-posti aadressi kinnitamise aknasse." + BREAK +
                        BREAK +
                        "<span style=\"font-size: 18px\"><b>" + userEmail.getPin() + "</b></span>" + BREAK + BREAK +
                        "Pane tähele, et kood on personaalne, ära saada seda teistele kasutajatele edasi." + BREAK +
                        "Küsimuste korral kirjuta: e-koolikott@hitsa.ee")
                .buildEmail();
    }

    public Email sendEmailToCreator(EmailToCreator emailToCreator) {
        return EmailBuilder.startingBlank()
                .from("E-koolikott", configuration.getString(EMAIL_NO_REPLY_ADDRESS))
                .to(emailToCreator.getUser().getFullName(), emailToCreator.getCreatorEmail())
                .withSubject("E-koolikott: Aineeksperdi küsimus")
                .withHTMLText("Aineekspert " +  emailToCreator.getSenderName() + " on esitanud küsimuse Teie õppevara kohta: " + emailToCreator.getLearningObjectTitle() + BREAK
                        + BREAK + emailToCreator.getMessage() + BREAK + BREAK +
                        "Aineeksperdile vastamiseks kirjutage e-posti aadressile:" + "<a href=\"mailto:" + emailToCreator.getSenderName() +
                        "\">" + emailToCreator.getSenderEmail() + "</a>")
                .buildEmail();
    }

    public Email sendEmailToSupportWhenSendEmailToCreatorFailed(EmailToCreator emailToCreator) {
        return EmailBuilder.startingBlank()
                .from("E-koolikott", emailToCreator.getSenderEmail())
                .to("HITSA Support", configuration.getString(EMAIL_ADDRESS))
                .withSubject("Aineeksperdi küsimus ebaõnnestunud ekirja saatmine")
                .withHTMLText("Kasutaja: " + emailToCreator.getSenderName() + ", " + emailToCreator.getSenderEmail() + BREAK
                        + BREAK + "Sisuga: " + emailToCreator.getMessage() + BREAK
                        + "Pöördumine saadeti: " + DateUtils.toStringWithoutMillis(emailToCreator.getSentAt()))
                .buildEmail();
    }
}
