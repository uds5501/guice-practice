package com.guice_practice.server.filters;

import com.google.inject.Singleton;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.logging.Logger;

@Singleton
public class LoggingFilter implements Filter
{
  private static final Logger log = Logger.getLogger(LoggingFilter.class.getName());

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException
  {
    HttpServletRequest req = (HttpServletRequest) servletRequest;
    long startTime = System.currentTimeMillis();
    filterChain.doFilter(req, servletResponse);
    long duration = System.currentTimeMillis() - startTime;
    log.info(String.format("Request processed [%s] %s in %d ms", req.getMethod(), req.getRequestURI(), duration));
  }
}
