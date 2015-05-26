package ee.hm.dop;

import static ee.hm.dop.utils.ConfigurationProperties.SERVER_PORT;
import static java.lang.String.format;

import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import ee.hm.dop.guice.GuiceInjector;
import ee.hm.dop.server.EmbeddedJetty;

@Singleton
public class ApplicationLauncher {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationLauncher.class);

    private static final int DEFAULT_SERVER_PORT = 8080;

    @Inject
    private static Configuration configuration;

    public static void startApplication() {
        GuiceInjector.init();

        if (ApplicationManager.isApplicationRunning()) {
            logger.warn("Unable to start. Application is already running.");
        } else {
            startServer();
            addShutdownHook();
            startCommandListener();
        }
    }

    private static void startCommandListener() {
        Thread commandListener = new Thread(new ApplicationManager.CommandListener());
        commandListener.setName("command-listener");
        commandListener.setDaemon(true);
        commandListener.start();
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                stopServer();
            }
        }, "shutdown-hook"));
    }

    private static void startServer() {
        try {
            int port = configuration.getInt(SERVER_PORT, DEFAULT_SERVER_PORT);
            logger.info(format("Starting application server on port [%s]", port));
            EmbeddedJetty.instance().start(port);
        } catch (Exception e) {
            logger.error("Error inicializing Jetty Server. Existing application.", e);
            System.exit(1);
        }
    }

    synchronized private static void stopServer() {
        logger.info("Stopping server...");
        try {
            EmbeddedJetty.instance().stop();
        } catch (Exception e) {
            logger.info("Error stopping server!", e);
        }
    }

    public static void stopApplication() throws Exception {
        GuiceInjector.init();
        ApplicationManager.stopApplication();
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || "start".equalsIgnoreCase(args[0])) {
            startApplication();
        } else if ("stop".equalsIgnoreCase(args[0])) {
            stopApplication();
        } else {
            logger.warn("Command does not exist. Use: start, stop or no command (default is start).");
        }
    }
}
