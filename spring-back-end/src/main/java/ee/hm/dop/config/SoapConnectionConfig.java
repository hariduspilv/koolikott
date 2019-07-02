package ee.hm.dop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;

@Configuration
@Profile("default")
public class SoapConnectionConfig {

    @Bean
    public synchronized SOAPConnection soapConnection() {
        try {
            return SOAPConnectionFactory.newInstance().createConnection();
        } catch (Exception e) {
            throw new RuntimeException("Error creating new SOAP connection.");
        }
    }
}
