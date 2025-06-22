package com.guice_practice.server.servlets;

import com.google.inject.Inject;
import com.guice_practice.server.models.User;
import com.guice_practice.server.services.UserService;
import com.guice_practice.server.utils.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class UserServlet extends HttpServlet
{
  private final UserService userService;

  @Inject
  public UserServlet(UserService userService) {this.userService = userService;}

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    resp.setContentType("application/json");
    String pathInfo = req.getPathInfo();

    if (pathInfo == null || pathInfo.equals("/")) {
      resp.getWriter().write(JsonUtil.toJson(userService.getAllUsers()));
    } else {
      String userId = pathInfo.substring(1); // Remove leading slash
      resp.getWriter().write(JsonUtil.toJson(userService.getUserById(userId)));
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    resp.setContentType("application/json");
    User user = JsonUtil.fromJson(req.getReader(), User.class);
    User created = userService.createUser(user);
    resp.setStatus(201);
    resp.getWriter().write(JsonUtil.toJson(created));
  }
}
