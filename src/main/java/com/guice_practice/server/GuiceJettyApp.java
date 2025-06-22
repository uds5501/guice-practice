package com.guice_practice.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.guice_practice.server.config.AppModule;
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
    Injector injector = Guice.createInjector(new AppModule());
    // injector me module daala

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
  }
}
