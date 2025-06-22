package com.guice_practice.server.services;

import com.guice_practice.server.models.User;

import java.util.List;

public interface UserService
{
  List<User> getAllUsers();

  User getUserById(String id);

  User createUser(User user);
}
