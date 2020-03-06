package plugins.poc.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import plugins.poc.web.config.AppContext;

public class PluginWebapp {

    public static void main(String[] args) throws Exception {
        new PluginWebapp().start(8080);
    }

    public void start(int port) throws Exception {

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.setClassLoader(PluginWebapp.class.getClassLoader());
            context.register(AppContext.class);
            context.refresh();
            context.registerShutdownHook();
            System.out.println("app context loaded");

            int maxThreads = 50;
            int minThreads = 10;
            int idleTimeout = 120;

            QueuedThreadPool thrPool = new QueuedThreadPool(maxThreads, minThreads, idleTimeout);

            Server server = new Server(thrPool);

            ServerConnector http = new ServerConnector(server);
            http.setHost("localhost");
            http.setPort(port);
            http.setIdleTimeout(30000);

            // Set the connector
            server.addConnector(http);

            // Add a single handler on context "/plug"
            ContextHandler pingHandler = new ContextHandler();
            pingHandler.setContextPath("/ping");
            pingHandler.setHandler(context.getBean(PingHandler.class));

            ContextHandlerCollection contextHandlers = new ContextHandlerCollection();
            contextHandlers.setHandlers(new Handler[]{pingHandler});

            //configure servlet handler
            ServletContextHandler servletHandler = new ServletContextHandler();
            servletHandler.setContextPath("/plug/*");

            //add regular servlet
            ServletHolder plugHolder = new ServletHolder(context.getBean(PluginServlet.class));
            servletHandler.addServlet(plugHolder, "/reg");

            //add async servlet
            ServletHolder asyncHolder = new ServletHolder(context.getBean(PluginServletAsync.class));
            servletHandler.addServlet(asyncHolder, "/");
            asyncHolder.setAsyncSupported(true);

            //configure resources handler
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);
            resourceHandler.setWelcomeFiles(new String[]{"plugin-dash.html"});
            resourceHandler.setResourceBase("www");

            // Add the handlers to the server.
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{resourceHandler, contextHandlers, servletHandler, new DefaultHandler()});
            server.setHandler(handlers);

            //buckle up
            server.start();
            server.join();
            //close context
            context.close();
        }
    }
}
