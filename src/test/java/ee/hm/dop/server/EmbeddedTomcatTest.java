package ee.hm.dop.server;

import org.junit.Test;

import ee.hm.dop.server.EmbeddedTomcat;

public class EmbeddedTomcatTest {

    private static final int TOMCAT_TEST_PORT = 111412;
    private EmbeddedTomcat embeddedTomcat = new EmbeddedTomcat();

    @Test
    public void startAndStop() throws Exception {
        embeddedTomcat.start(TOMCAT_TEST_PORT);
        embeddedTomcat.stop();
    }

    @Test
    public void doubleStart() throws Exception {
        embeddedTomcat.start(111412);
        embeddedTomcat.start(111412);

        embeddedTomcat.stop();
    }

    @Test
    public void stopWhenTomcatIsNotStarted() throws Exception {
        embeddedTomcat.stop();
    }
}
