package com.guice_practice.server.services;

import com.guice_practice.server.models.Product;

import java.util.List;

public interface ProductService
{
  List<Product> getAllProducts();

  Product getProductById(String id);

  Product createProduct(Product product);
}
