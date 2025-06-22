package com.guice_practice.server.services;

import com.google.inject.Singleton;
import com.guice_practice.server.models.Notification;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class NotificationServiceImpl implements NotificationService
{
  private final List<Notification> notifications = new ArrayList<>(List.of(
      new Notification("1", "1", "Welcome to the platform!", "welcome"),
      new Notification("2", "2", "Your order has been shipped", "order_update")
  ));

  @Override
  public List<Notification> getAllNotifications()
  {
    return new ArrayList<>(notifications);
  }

  @Override
  public Notification getNotificationById(String id)
  {
    return notifications.stream().filter(n -> n.getId().equals(id)).findFirst().orElse(null);
  }

  @Override
  public Notification sendNotification(Notification notification)
  {
    notification.setId(String.valueOf(System.currentTimeMillis()));
    notification.setTimestamp(System.currentTimeMillis());
    notifications.add(notification);
    return notification;
  }

  @Override
  public Notification updateNotification(Notification notification)
  {
    for (int i = 0; i < notifications.size(); i++) {
      if (notifications.get(i).getId().equals(notification.getId())) {
        notifications.set(i, notification);
        return notification;
      }
    }
    return null;
  }
}