package ee.hm.dop.server;

import java.net.URI;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.GuiceFilter;

public class EmbeddedJetty {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddedJetty.class);

    private static final int DEFAULT_PORT = 8080;

    private Server server;

    public void start() throws Exception {
        start(DEFAULT_PORT);
    }

    public void start(int port) throws Exception {
        synchronized (this) {
            if (server == null) {
                logger.info("Starting jetty on port " + port);
                server = new Server(port);
                server.setHandler(createServletContextHandler());
                server.start();
            }
        }
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/");

        addFilters(servletContextHandler);
        configureDynamicContentServlet(servletContextHandler);
        configureStaticContentServlet(servletContextHandler);
        return servletContextHandler;
    }

    private void configureStaticContentServlet(ServletContextHandler servletContextHandler) {
        servletContextHandler.addServlet(DefaultServlet.class, "/");
        servletContextHandler.setResourceBase(getClass().getClassLoader().getResource("webapp").toExternalForm());
        servletContextHandler.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
    }

    private void configureDynamicContentServlet(ServletContextHandler servletContextHandler) {
        ServletHolder servlet = new ServletHolder(new ServletContainer());
        servlet.setInitParameter("javax.ws.rs.Application", "ee.hm.dop.config.DOPApplication");
        servletContextHandler.addServlet(servlet, "/rest/*");
    }

    private void addFilters(ServletContextHandler servletContextHandler) {
        // Filter to inject object into other filters. Must be above all others.
        servletContextHandler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
    }

    public void stop() throws Exception {
        synchronized (this) {
            if (server == null) {
                return;
            }

            if (server.getState() != Server.STOPPED) {
                server.stop();
            }

            server.destroy();
            server = null;
        }
    }

    public URI getBaseUri() {
        return server.getURI();
    }
}