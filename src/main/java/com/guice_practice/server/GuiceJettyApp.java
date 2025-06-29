package com.guice_practice.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.guice_practice.server.commons.Lifecycle;
import com.guice_practice.server.commons.LifecycleModule;
import com.guice_practice.server.config.AppModule;
import com.guice_practice.server.services.stateful.PostgresModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hello world!
 */
public class GuiceJettyApp
{
  public static void main(String[] args) throws Exception
  {
    Injector injector = Guice.createInjector(
        new PostgresModule(),
        new LifecycleModule(),
        new AppModule()
    );
    // init the lifecycle
    Lifecycle lifecycle = injector.getInstance(Lifecycle.class);
    lifecycle.start();

    final Thread hook = new Thread(lifecycle::stop);
    Runtime.getRuntime().addShutdownHook(hook);


    Server server = new Server(8188);
    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    context.addFilter(GuiceFilter.class, "/*", null);
    context.addServlet(DefaultServlet.class, "/");
    context.getServletContext().setAttribute(Injector.class.getName(), injector);

    // servlets + guice is added to server here
    server.setHandler(context);
    System.out.println("Starting server on port 8188...");
    server.start();
    server.join();
    try {
      lifecycle.join();
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Lifecycle interrupted: " + e.getMessage());
    }
  }
}
