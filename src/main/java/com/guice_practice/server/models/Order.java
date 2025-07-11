package com.guice_practice.server.models;

public class Order
{
  private String id;
  private String userId;
  private String productId;
  private int quantity;

  public Order() {}

  public Order(String id, String userId, String productId, int quantity)
  {
    this.id = id;
    this.userId = userId;
    this.productId = productId;
    this.quantity = quantity;
  }

  public String getId() {return id;}

  public void setId(String id) {this.id = id;}

  public String getUserId() {return userId;}

  public void setUserId(String userId) {this.userId = userId;}

  public String getProductId() {return productId;}

  public void setProductId(String productId) {this.productId = productId;}

  public int getQuantity() {return quantity;}

  public void setQuantity(int quantity) {this.quantity = quantity;}
}
