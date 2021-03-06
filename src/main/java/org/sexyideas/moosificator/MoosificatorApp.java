package org.sexyideas.moosificator;

import io.keen.client.java.KeenClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.TracingConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class MoosificatorApp extends ResourceConfig {
    public MoosificatorApp() {
        register(MooseResource.class);
        KeenClient.initialize();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter("javax.ws.rs.Application", "org.sexyideas.moosificator.MoosificatorApp");
        jerseyServlet.setInitParameter("com.sun.jersey.config.property.packages", "jetty");

        ServletHolder staticServlet = context.addServlet(DefaultServlet.class, "/*");
        staticServlet.setInitParameter("resourceBase", "src/main/webapp");
        staticServlet.setInitParameter("pathInfoOnly", "true");

        try {
            server.start();
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}