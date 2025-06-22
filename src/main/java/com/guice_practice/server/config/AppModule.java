package com.guice_practice.server.config;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.ServletModule;
import com.guice_practice.server.annotations.JsonContent;
import com.guice_practice.server.filters.LoggingFilter;
import com.guice_practice.server.interceptors.JsonContentInterceptor;
import com.guice_practice.server.services.NotificationService;
import com.guice_practice.server.services.NotificationServiceImpl;
import com.guice_practice.server.services.ProductService;
import com.guice_practice.server.services.ProductServiceImpl;
import com.guice_practice.server.services.UserService;
import com.guice_practice.server.services.UserServiceImpl;
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

    bind(UserService.class).to(UserServiceImpl.class);
    bind(ProductService.class).to(ProductServiceImpl.class);
    bind(NotificationService.class).to(NotificationServiceImpl.class);

    JsonContentInterceptor interceptor = new JsonContentInterceptor();
    requestInjection(interceptor);
    bindInterceptor(Matchers.any(), Matchers.annotatedWith(JsonContent.class), interceptor);
  }
}
