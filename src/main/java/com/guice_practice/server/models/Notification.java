package com.guice_practice.server.models;

public class Notification
{
  private String id;
  private String userId;
  private String message;
  private String type;
  private boolean read;
  private long timestamp;

  public Notification() {}

  public Notification(String id, String userId, String message, String type)
  {
    this.id = id;
    this.userId = userId;
    this.message = message;
    this.type = type;
    this.read = false;
    this.timestamp = System.currentTimeMillis();
  }

  public String getId() {return id;}

  public void setId(String id) {this.id = id;}

  public String getUserId() {return userId;}

  public void setUserId(String userId) {this.userId = userId;}

  public String getMessage() {return message;}

  public void setMessage(String message) {this.message = message;}

  public String getType() {return type;}

  public void setType(String type) {this.type = type;}

  public boolean isRead() {return read;}

  public void setRead(boolean read) {this.read = read;}

  public long getTimestamp() {return timestamp;}

  public void setTimestamp(long timestamp) {this.timestamp = timestamp;}
}
