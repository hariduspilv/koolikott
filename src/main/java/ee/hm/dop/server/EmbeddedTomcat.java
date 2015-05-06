package ee.hm.dop.server;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a simple way to start and stop tomcat8.
 * 
 */
public class EmbeddedTomcat implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedTomcat.class);

    private static final int DEFAULT_PORT = 8080;

    private static Tomcat tomcat;

    public void start() throws Exception {
        start(DEFAULT_PORT);
    }

    synchronized public void start(int port) throws Exception {
        if (tomcat != null) {
            return;
        }

        tomcat = new Tomcat();

        tomcat.setPort(port);

        // Defines a web application context.
        String webappDir = EmbeddedTomcat.class.getClassLoader().getResource("webapp").getFile();
        Context context = tomcat.addWebapp("", webappDir);

        // Define and bind web.xml file location.
        File webXml = new File(webappDir, "WEB-INF/web.xml");
        context.setConfigFile(webXml.toURI().toURL());

        tomcat.start();
    }

    synchronized public final void stop() throws LifecycleException {
        if (tomcat == null) {
            return;
        }

        LifecycleState serverState = tomcat.getServer().getState();
        if (serverState != LifecycleState.DESTROYED) {
            if (serverState != LifecycleState.STOPPED) {
                tomcat.stop();
            }

            tomcat.destroy();
        }

        tomcat = null;
    }

    @Override
    public void run() {
        try {
            start();
            tomcat.getServer().await();
        } catch (Exception e) {
            logger.error("Tomcat error.", e);
        }
    }
}
