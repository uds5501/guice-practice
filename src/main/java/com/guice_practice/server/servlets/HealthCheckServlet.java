package com.guice_practice.server.servlets;


import com.google.inject.Singleton;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Singleton
public class HealthCheckServlet extends HttpServlet
{
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    resp.setContentType("application/json");
    resp.getWriter().write("{\"status\":\"OK\"}");
  }
}
