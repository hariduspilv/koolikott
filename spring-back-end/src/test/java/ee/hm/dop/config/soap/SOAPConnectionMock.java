package ee.hm.dop.config.soap;

import ee.hm.dop.config.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.HashMap;
import java.util.Map;

import static ee.hm.dop.utils.ConfigurationProperties.EHIS_ENDPOINT;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_ENDPOINT;

@Component
@Profile("it")
public class SOAPConnectionMock extends SOAPConnection {

    private Map<String, SOAPConnectionMockI> endpoints = new HashMap<>();

    public SOAPConnectionMock(Configuration configuration) {
        endpoints.put(configuration.getString(MOBILEID_ENDPOINT), new MobileIdSOAPConnection());
        endpoints.put(configuration.getString(EHIS_ENDPOINT), new EhisSOAPConnection());
    }

    @Override
    public SOAPMessage call(SOAPMessage request, Object to) throws SOAPException {
        SOAPConnectionMockI soapConnection = endpoints.get(to.toString());

        if (soapConnection == null) {
            throw new RuntimeException("Endpoint is not defined: " + to);
        }

        return soapConnection.call(request, to);
    }

    @Override
    public void close() throws SOAPException {
    }
}