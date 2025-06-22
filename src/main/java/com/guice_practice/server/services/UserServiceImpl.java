package com.guice_practice.server.services;

import com.google.inject.Singleton;
import com.guice_practice.server.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class UserServiceImpl implements UserService
{
  ConcurrentHashMap<String, User> userDatabase = new ConcurrentHashMap<>();

  @Override
  public List<User> getAllUsers()
  {
    return new ArrayList<>(userDatabase.values());
  }

  @Override
  public User getUserById(String id)
  {
    return userDatabase.get(id);
  }

  @Override
  public User createUser(User user)
  {
    if (user.getId() == null || user.getId().isEmpty()) {
      throw new IllegalArgumentException("User ID cannot be null or empty");
    }
    if (userDatabase.containsKey(user.getId())) {
      throw new IllegalArgumentException("User with this ID already exists");
    }
    userDatabase.put(user.getId(), user);
    return user;
  }
}
