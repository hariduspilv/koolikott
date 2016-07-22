package ee.hm.dop.guice.provider;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;

import ee.hm.dop.common.test.GuiceTestRunner;
import ee.hm.dop.guice.GuiceInjector;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GuiceTestRunner.class)
public class HttpClientProviderTest {

    private HttpClientProvider httpClientProvider;

    @Before
    public void setup() {
        httpClientProvider = GuiceInjector.getInjector().getInstance(HttpClientProvider.class);
    }

    @Test
    public void get() {
        Client client = httpClientProvider.get();
        assertNotNull(client);
        Configuration configuration = client.getConfiguration();
        assertTrue(configuration.isRegistered(JacksonFeature.class));
        assertTrue(configuration.isRegistered(ObjectMapperProvider.class));
    }

    @Test
    public void getAlwaysReturnSameObject() {
        Client client = httpClientProvider.get();

        for (int i = 0; i < 10; i++) {
            assertSame(client, httpClientProvider.get());
        }
    }
}
