package com.guice_practice.server.services.stateful;

import com.google.inject.Inject;
import com.guice_practice.server.commons.ALifeEnds;
import com.guice_practice.server.commons.LifecycleBegins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class PostgresDatabaseServiceImpl implements DatabaseService
{
  private static Logger logger = LoggerFactory.getLogger(PostgresDatabaseServiceImpl.class);

  @Inject
  private PostgresConfig config;
  private Connection connection;

  public PostgresDatabaseServiceImpl(PostgresConfig config)
  {
    this.config = config;
  }

  @LifecycleBegins
  private void connect() throws Exception
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
  private void disconnect() throws Exception
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
    return false;
  }
}
