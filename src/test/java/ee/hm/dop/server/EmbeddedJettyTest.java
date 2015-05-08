package ee.hm.dop.server;

import org.junit.Before;
import org.junit.Test;

public class EmbeddedJettyTest {

    // Port 0 tells Jetty to use any available port
    private static final int ANY_AVAILABLE_PORT = 0;
    private EmbeddedJetty server;

    @Before
    public void setup() {
        server = EmbeddedJetty.instance();
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

        server.stop();
    }

    @Test
    public void stopWhenTomcatIsNotStarted() throws Exception {
        server.stop();
    }
}
