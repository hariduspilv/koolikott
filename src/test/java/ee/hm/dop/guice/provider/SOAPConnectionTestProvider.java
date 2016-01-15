package ee.hm.dop.guice.provider;

import javax.inject.Inject;
import javax.xml.soap.SOAPConnection;

import org.apache.commons.configuration.Configuration;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import ee.hm.dop.guice.provider.mock.soap.SOAPConnectionMock;

/**
 * Guice provider of SOAPConnection.
 */
@Singleton
public class SOAPConnectionTestProvider implements Provider<SOAPConnection> {

    @Inject
    private Configuration configuration;

    @Override
    public synchronized SOAPConnection get() {
        return new SOAPConnectionMock(configuration);
    }
}