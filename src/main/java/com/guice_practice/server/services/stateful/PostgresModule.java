package com.guice_practice.server.services.stateful;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class PostgresModule extends AbstractModule
{
  @Provides
  public static PostgresConfig getFromEnv()
  {
    return new PostgresConfig(
        System.getenv("POSTGRES_HOST"),
        Integer.parseInt(System.getenv("POSTGRES_PORT")),
        System.getenv("POSTGRES_DB"),
        System.getenv("POSTGRES_USER"),
        System.getenv("POSTGRES_PASSWORD")
    );
  }
}
