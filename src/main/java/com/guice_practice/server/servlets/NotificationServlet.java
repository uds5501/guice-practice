package com.guice_practice.server.servlets;

import com.google.inject.Inject;
import com.guice_practice.server.annotations.JsonContent;
import com.guice_practice.server.models.Notification;
import com.guice_practice.server.services.NotificationService;
import com.guice_practice.server.utils.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class NotificationServlet extends HttpServlet
{
  private final NotificationService notificationService;

  @Inject
  public NotificationServlet(NotificationService notificationService)
  {
    this.notificationService = notificationService;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    response.setContentType("application/json");
    List<Notification> notifications = notificationService.getAllNotifications();
    response.setStatus(201);
    response.getWriter().write(JsonUtil.toJson(notifications));
  }

  @Override
  @JsonContent
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    response.setContentType("application/json");
    Notification notification = JsonUtil.fromJson(request.getReader(), Notification.class);
    Notification created = notificationService.sendNotification(notification);
    response.setStatus(201);
    response.getWriter().write(JsonUtil.toJson(created));
  }

  @Override
  @JsonContent
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException
  {
    String pathInfo = req.getPathInfo();
    if (pathInfo == null || pathInfo.equals("/")) {
      resp.setStatus(400);
      resp.getWriter().write("{\"error\":\"Notification ID required\"}");
      return;
    }

    String notificationId = pathInfo.substring(1);
    Notification notification = JsonUtil.fromJson(req.getReader(), Notification.class);
    notification.setId(notificationId);

    Notification updated = notificationService.updateNotification(notification);
    if (updated != null) {
      resp.getWriter().write(JsonUtil.toJson(updated));
    } else {
      resp.setStatus(404);
      resp.getWriter().write("{\"error\":\"Notification not found\"}");
    }
  }
}
