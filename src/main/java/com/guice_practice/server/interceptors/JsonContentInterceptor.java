package com.guice_practice.server.interceptors;

import com.google.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.IOException;

public class JsonContentInterceptor implements MethodInterceptor
{

  @Inject
  private HttpServletRequest request;

  @Inject
  private HttpServletResponse response;

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable
  {
    if (request != null && response != null) {
      String method = request.getMethod();
      if ("POST".equals(method) || "PUT".equals(method)) {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.contains("application/json")) {
          response.setStatus(400);
          response.setContentType("application/json");
          try {
            response.getWriter().write("{\"error\":\"Invalid content type. Expected application/json.\"}");
          }
          catch (IOException e) {
            // Handle the exception if writing to the response fails
            e.printStackTrace();
          }
          return null;
        }
      }
      response.setContentType("application/json");
      response.setHeader("X-API-Version", "1.0");
      response.setHeader("X-Content-Handler", "JsonContent");
    }
    try {
      return invocation.proceed();
    }
    catch (Exception e) {
      if (response != null) {
        response.setStatus(400);
        response.setContentType("application/json");
        try {
          response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
        catch (IOException ioException) {
          // Handle the exception if writing to the response fails
          ioException.printStackTrace();
        }
      }
      return null;
    }
  }
}
