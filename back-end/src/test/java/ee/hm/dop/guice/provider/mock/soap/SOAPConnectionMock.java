package ee.hm.dop.guice.provider.mock.soap;

import static ee.hm.dop.utils.ConfigurationProperties.EHIS_ENDPOINT;
import static ee.hm.dop.utils.ConfigurationProperties.MOBILEID_ENDPOINT;

import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.configuration.Configuration;

public class SOAPConnectionMock extends SOAPConnection {

    private Map<Object, SOAPConnectionMockI> endpoints = new HashMap<>();

    public SOAPConnectionMock(Configuration configuration) {
        endpoints.put(configuration.getString(MOBILEID_ENDPOINT), new MobileIdSOAPConnection());
        endpoints.put(configuration.getString(EHIS_ENDPOINT), new EhisSOAPConnection());
    }

    @Override
    public SOAPMessage call(SOAPMessage request, Object to) throws SOAPException {
        SOAPConnectionMockI soapConnection = endpoints.get(to);

        if (soapConnection == null) {
            throw new RuntimeException("Endpoint does not defined: " + to);
        }

        return soapConnection.call(request, to);
    }

    @Override
    public void close() throws SOAPException {
    }
}