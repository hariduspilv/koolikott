package ee.hm.dop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.server.EmbeddedJetty;

public class ApplicationLauncher {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationLauncher.class);

    public static void main(String[] args) {
        startServer();
    }

    private static void startServer() {
        try {
            logger.info("Starting application server");
            new EmbeddedJetty().start();
        } catch (Exception e) {
            logger.error("Error inicializing Jetty Server. Existing application.", e);
            System.exit(1);
        }
    }
}
