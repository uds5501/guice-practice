package com.guice_practice.server.config;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;
import com.guice_practice.server.filters.LoggingFilter;
import com.guice_practice.server.servlets.HealthCheckServlet;
import com.guice_practice.server.servlets.NotificationServlet;
import com.guice_practice.server.servlets.UserServlet;

public class AppModule extends AbstractModule
{
  @Override
  protected void configure()
  {
    install(new ServletModule()
    {
      @Override
      protected void configureServlets()
      {
        serve("/api/health").with(HealthCheckServlet.class);
        serve("/api/notifications/*").with(NotificationServlet.class);
        serve("/api/users/*").with(UserServlet.class);

        filter("/api/*").through(LoggingFilter.class);
      }
    });
  }
}
