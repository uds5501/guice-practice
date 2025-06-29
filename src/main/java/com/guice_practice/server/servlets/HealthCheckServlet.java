package com.guice_practice.server.servlets;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.guice_practice.server.services.stateful.DatabaseService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Singleton
public class HealthCheckServlet extends HttpServlet
{
  private final DatabaseService databaseService;

  @Inject
  public HealthCheckServlet(DatabaseService databaseService)
  {
    this.databaseService = databaseService;
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    resp.setContentType("application/json");

    boolean allHealthy = databaseService.isConnected();

    String status = allHealthy ? "OK" : "DEGRADED";
    resp.setStatus(allHealthy ? 200 : 503);

    resp.getWriter().write(String.format(
        "{\"status\":\"%s\",\"timestamp\":%d,\"services\":{\"database\":%b}}",
        status, System.currentTimeMillis(),
        databaseService.isConnected()
    ));
  }
}
