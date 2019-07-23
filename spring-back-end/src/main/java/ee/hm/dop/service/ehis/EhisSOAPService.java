package ee.hm.dop.service.ehis;

import ee.hm.dop.config.Configuration;
import ee.hm.dop.model.ehis.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;

import static ee.hm.dop.utils.ConfigurationProperties.*;
import static java.lang.String.format;

@Service
@Transactional
public class EhisSOAPService implements IEhisSOAPService {

    private static Logger logger = LoggerFactory.getLogger(EhisSOAPService.class);
    @Inject
    private EhisParser ehisParser;
    @Inject
    private Configuration configuration;
    @Inject
    private Environment environment;
    @Inject
    private SOAPConnection connection;
    @Inject
    private EhisV6RequestBuilder ehisV6RequestBuilder;
    @Inject
    private EhisV6ResponseAnalyzer ehisV6ResponseAnalyzer;

    @Override
    public Person getPersonInformation(String idCode) {
        try {
            SOAPMessage message = ehisV6RequestBuilder.createGetPersonInformationSOAPMessage(idCode);
            if (message != null) {

                if (logger.isInfoEnabled()) {
                    log(message, "Sending message to EHIS: %s");
                }

                SOAPMessage response = sendSOAPMessage(message);

                if (logger.isInfoEnabled()) {
                    log(response, "Received response from EHIS: %s");
                }

                if (environment.acceptsProfiles(Profiles.of("it")) && response == null) {
                    return null;
                }

                String xmlResponse = ehisV6ResponseAnalyzer.parseSOAPResponse(response);

                logger.info(format("Received response from EHIS: %s", xmlResponse));
                return ehisParser.parse(xmlResponse);
            }
            else return new Person();

        } catch (Exception e) {
            if (environment.acceptsProfiles(Profiles.of("it", "test"))) {
                logger.error("Error getting User information from EHIS. {}", e.getMessage(), e);
                return null;
            }
            logger.error("Error getting User information from EHIS. {}", e.getMessage(), e);
            return null;
        }
    }

    private void log(SOAPMessage message, String msg) throws SOAPException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (message != null) {
            message.writeTo(out);
        }
        String strMsg = new String(out.toByteArray(), StandardCharsets.UTF_8);
        logger.info(format(msg, strMsg));
    }

    private SOAPMessage sendSOAPMessage(SOAPMessage message) throws SOAPException, IOException {
        String endpoint = configuration.getString(XROAD_EHIS_V6_ENDPOINT);

//        return connection.call(message, endpoint);
        try {
            return connection.call(message, addSoapTimeout(endpoint));
        } catch (MalformedURLException e) {
            logger.error("MalformedURLException", e);
            throw e;
        } catch (IOException e) {
            logger.error("IOException", e);
            throw e;
        }
    }

    private URL addSoapTimeout(String endpoint) throws IOException {
        return new URL(new URL(endpoint), endpoint,
                new URLStreamHandler() {
                    @Override
                    protected URLConnection openConnection(URL url) throws IOException {
                        URL target = new URL(url.toString());
                        URLConnection connection = target.openConnection();
                        connection.setConnectTimeout(configuration.getInt(XROAD_EHIS_TIMEOUT_CONNECT));
                        connection.setReadTimeout(configuration.getInt(XROAD_EHIS_TIMEOUT_READ));
                        return (connection);
                    }
                });
    }
}
