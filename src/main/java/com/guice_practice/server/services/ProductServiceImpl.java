package com.guice_practice.server.services;

import com.google.inject.Singleton;
import com.guice_practice.server.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ProductServiceImpl implements ProductService
{
  private final Map<String, Product> productDatabase = new ConcurrentHashMap<>();

  @Override
  public List<Product> getAllProducts()
  {
    return new ArrayList<>(productDatabase.values());
  }

  @Override
  public Product getProductById(String id)
  {
    return productDatabase.get(id);
  }

  @Override
  public Product createProduct(Product product)
  {
    if (product == null || product.getId() == null || product.getId().isEmpty()) {
      throw new IllegalArgumentException("Product ID cannot be null or empty");
    }
    if (productDatabase.containsKey(product.getId())) {
      throw new IllegalArgumentException("Product with this ID already exists");
    }
    productDatabase.put(product.getId(), product);
    return product;
  }
}
