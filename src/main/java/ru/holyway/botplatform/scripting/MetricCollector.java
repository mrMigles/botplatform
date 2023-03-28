package ru.holyway.botplatform.scripting;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MetricCollector {

  private Map<String, String> logs;
  private Map<String, Cache<Integer, Long>> executions;

  private static volatile MetricCollector instance;

  public static MetricCollector getInstance() {
    if (instance == null) {
      synchronized (MetricCollector.class) {
        if (instance == null) {
          instance = new MetricCollector();
        }
      }
    }
    return instance;
  }

  private MetricCollector() {
    logs = new HashMap<>();
    executions = new HashMap<>();
  }


  public void saveLog(final String chatId, Script script, final Throwable exception) {
    final String log = "{\"ts\": " + System.currentTimeMillis() + ", \"script\": \"" + script.getStringScript().replaceAll("\\\\\\$", "\\$").replaceAll("\\.owner\\(\\d*\\)", "") + "\", \"message\": \"" + getFullMessage(exception) + "\"}";
    logs.put(chatId, log);
  }

  private static String getFullMessage(Throwable throwable) {
    String message = throwable.getMessage();
    Throwable cause = throwable.getCause();

    if (cause != null) {
      String causeMessage = getFullMessage(cause);
      if (message != null) {
        message += "; " + causeMessage;
      } else {
        message = causeMessage;
      }
    }

    return message;
  }

  public String getLog(final String chatId) {
    return logs.get(chatId);
  }

  public void trackCall(final String chatId, final Integer executionTime) {
    if (!executions.containsKey(chatId)) {
      executions.put(chatId, CacheBuilder.newBuilder()
          .expireAfterWrite(1, TimeUnit.HOURS)
          .build());
    }
    executions.get(chatId).put(executionTime, System.currentTimeMillis());
  }

  private List<Integer> getExecutions(final String chatId) {
    return executions.containsKey(chatId) ? new ArrayList<>(executions.get(chatId).asMap().keySet()) : Collections.emptyList();
  }

  private Integer getExecutionCount(final String chatId) {
    return getExecutions(chatId).size();
  }

  private Long getExecutionAverageTime(final String chatId) {
    return Math.round(getExecutions(chatId).stream().mapToInt(Integer::intValue).average().orElse(0.0));
  }

  private Integer getExecutionMaxTime(final String chatId) {
    return Collections.max(getExecutions(chatId));
  }

  private Integer getExecutionTime(final String chatId) {
    return getExecutions(chatId).stream().mapToInt(Integer::intValue).sum();
  }

  public String getExecutionInfo() {
    StringBuilder message = new StringBuilder("Last hour metrics: \n\n");
    for (final String chatId : executions.keySet()) {
      message.append(chatId).append(": count: ").append(getExecutionCount(chatId)).append(", full: ").append(getExecutionTime(chatId)).append(" ms, max: ").append(getExecutionMaxTime(chatId)).append(" ms, avg: ").append(getExecutionAverageTime(chatId)).append(" ms\n");
    }
    return message.toString();
  }
}
