package com.guice_practice.server.interceptors;

import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.IOException;

public class JsonContentInterceptor implements MethodInterceptor
{

  @Inject
  private Provider<HttpServletRequest> requestProvider;

  @Inject
  private Provider<HttpServletResponse> responseProvider;

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable
  {
    if (requestProvider.get() != null && responseProvider.get() != null) {
      String method = requestProvider.get().getMethod();
      if ("POST".equals(method) || "PUT".equals(method)) {
        String contentType = requestProvider.get().getContentType();
        if (contentType == null || !contentType.contains("application/json")) {
          responseProvider.get().setStatus(400);
          responseProvider.get().setContentType("application/json");
          try {
            responseProvider.get()
                            .getWriter()
                            .write("{\"error\":\"Invalid content type. Expected application/json.\"}");
          }
          catch (IOException e) {
            // Handle the exception if writing to the response fails
            e.printStackTrace();
          }
          return null;
        }
      }
      responseProvider.get().setContentType("application/json");
      responseProvider.get().setHeader("X-API-Version", "1.0");
      responseProvider.get().setHeader("X-Content-Handler", "JsonContent");
    }
    try {
      return invocation.proceed();
    }
    catch (Exception e) {
      if (responseProvider.get() != null) {
        responseProvider.get().setStatus(400);
        responseProvider.get().setContentType("application/json");
        try {
          responseProvider.get().getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
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
