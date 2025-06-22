package com.guice_practice.server.services;

import com.guice_practice.server.models.Notification;

import java.util.List;

public interface NotificationService
{
  List<Notification> getAllNotifications();

  Notification getNotificationById(String id);

  Notification sendNotification(Notification notification);

  Notification updateNotification(Notification notification);
}
