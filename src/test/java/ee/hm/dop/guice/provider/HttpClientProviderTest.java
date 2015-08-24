package ee.hm.dop.guice.provider;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Injector;

import ee.hm.dop.common.test.GuiceTestRunner;
import ee.hm.dop.guice.GuiceInjector;

@RunWith(GuiceTestRunner.class)
public class HttpClientProviderTest {

    @Test
    public void get() {
        Client client = GuiceInjector.getInjector().getInstance(Client.class);

        assertNotNull(client);
        Configuration configuration = client.getConfiguration();
        assertTrue(configuration.isRegistered(JacksonFeature.class));
        assertTrue(configuration.isRegistered(ObjectMapperProvider.class));
    }

    @Test
    public void getAlwaysReturnSameObject() {
        Injector injector = GuiceInjector.getInjector();
        Client client = injector.getInstance(Client.class);

        for (int i = 0; i < 10; i++) {
            assertSame(client, injector.getInstance(Client.class));
        }
    }
}
