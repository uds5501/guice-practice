package com.guice_practice.server.services.stateful;

import com.google.inject.Provides;

public class PostgresConfig
{
  private final String host;
  private final int port;
  private final String databaseName;
  private final String username;
  private final String password;

  public PostgresConfig(String host, int port, String databaseName, String username, String password)
  {
    this.host = host;
    this.port = port;
    this.databaseName = databaseName;
    this.username = username;
    this.password = password;
  }

  public String getHost()
  {
    return host;
  }

  public int getPort()
  {
    return port;
  }

  public String getDatabaseName()
  {
    return databaseName;
  }

  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }
}
