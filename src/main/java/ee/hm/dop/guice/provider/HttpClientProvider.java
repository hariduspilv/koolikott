package ee.hm.dop.guice.provider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Provider for Client.
 */
@Singleton
public class HttpClientProvider implements Provider<Client> {

    private Client client;

    @Override
    public Client get() {
        if (client == null) {
            initClient();
        }

        return client;
    }

    /**
     * Protected for test purpose
     */
    protected void initClient() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.READ_TIMEOUT, 60000); // ms
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 60000); // ms

        client = ClientBuilder.newClient(clientConfig);
        client.register(JacksonFeature.class);
        client.register(ObjectMapperProvider.class);
    }
}
