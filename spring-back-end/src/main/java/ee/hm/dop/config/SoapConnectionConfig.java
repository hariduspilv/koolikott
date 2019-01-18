package ee.hm.dop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;

@Configuration
public class SoapConnectionConfig {

    @Bean
    public synchronized SOAPConnection soapConnection() {
        try {
            SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
            return connectionFactory.createConnection();
        } catch (Exception e) {
            throw new RuntimeException("Error creating new SOAP connection.");
        }
    }
}
