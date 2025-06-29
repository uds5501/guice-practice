package com.guice_practice.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;

public class JsonUtil
{
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String toJson(Object obj)
  {
    try {
      return objectMapper.writeValueAsString(obj);
    }
    catch (Exception e) {
      return "{\"error\":\"Failed to convert object to JSON\"}";
    }
  }

  public static <T> T fromJson(BufferedReader jsonStr, Class<T> clazz) {
    try {
      return objectMapper.readValue(jsonStr, clazz);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
