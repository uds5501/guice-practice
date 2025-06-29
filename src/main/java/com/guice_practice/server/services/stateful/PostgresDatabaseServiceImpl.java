package com.guice_practice.server.services.stateful;

import com.google.inject.Inject;
import com.guice_practice.server.commons.ALifeEnds;
import com.guice_practice.server.commons.LifecycleBegins;
import com.guice_practice.server.commons.ManagedGlobalLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@ManagedGlobalLifecycle
public class PostgresDatabaseServiceImpl implements DatabaseService
{
  private static final Logger logger = LoggerFactory.getLogger(PostgresDatabaseServiceImpl.class);

  private final PostgresConfig config;
  private Connection connection;

  @Inject
  public PostgresDatabaseServiceImpl(PostgresConfig config)
  {
    this.config = config;
  }

  @LifecycleBegins
  public void connect() throws Exception
  {
    String host = config.getHost();
    String user = config.getUsername();
    String password = config.getPassword();
    this.connection = DriverManager.getConnection(
        String.format("jdbc:postgresql://%s:%d/%s", host, config.getPort(), config.getDatabaseName()),
        user, password
    );
  }

  @ALifeEnds
  public void disconnect() throws Exception
  {
    if (connection != null && !connection.isClosed()) {
      connection.close();
      logger.info("Disconnected from Postgres database.");
    } else {
      logger.warn("Connection was already closed or null.");
    }
  }

  @Override
  public boolean isConnected()
  {
    try {
      return this.connection != null && !this.connection.isClosed();
    }
    catch (SQLException e) {
      return false;
    }
  }
}
