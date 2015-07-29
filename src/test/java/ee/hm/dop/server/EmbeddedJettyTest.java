package ee.hm.dop.server;

import static ee.hm.dop.utils.ConfigurationProperties.SERVER_PORT;

import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ee.hm.dop.common.test.GuiceTestRunner;

@RunWith(GuiceTestRunner.class)
public class EmbeddedJettyTest {

    // Port 0 tells Jetty to use any available port
    private static final int ANY_AVAILABLE_PORT = 0;

    @Inject
    private static Configuration configuration;

    private static EmbeddedJetty server;
    private static boolean wasRunning;

    @BeforeClass
    public static void verifyServerIsAlreadyRunning() throws Exception {
        server = EmbeddedJetty.instance();
        wasRunning = server.isRunning();

        // Ensure that server is not running before starting the tests.
        server.stop();
    }

    @AfterClass
    public static void startServerIfItWasRunning() throws Exception {
        if (wasRunning) {
            int port = configuration.getInt(SERVER_PORT);
            server.start(port);
        }
    }

    /**
     * Tests should always stop server after running
     * 
     * @throws Exception
     */
    @After
    public void stopServer() throws Exception {
        server.stop();
    }

    @Test
    public void startAndStop() throws Exception {
        server.start(ANY_AVAILABLE_PORT);
        server.stop();
    }

    @Test
    public void doubleStart() throws Exception {
        server.start(ANY_AVAILABLE_PORT);
        server.start(ANY_AVAILABLE_PORT);
    }

    @Test
    public void stopWhenTomcatIsNotStarted() throws Exception {
        server.stop();
    }
}
