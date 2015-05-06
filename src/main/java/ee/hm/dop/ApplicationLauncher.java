package ee.hm.dop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.server.EmbeddedTomcat;

public class ApplicationLauncher {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationLauncher.class);

    public static void main(String[] args) {
        startServer();
    }

    private static void startServer() {
        logger.info("Starting application server");

        EmbeddedTomcat server = new EmbeddedTomcat();
        (new Thread(server, "Embedded Tomcat for DOP")).start();
    }
}
