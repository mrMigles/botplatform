package ru.holyway.botplatform.core;

/**
 * Created by Sergey on 1/17/2017.
 */
public interface Bot {

  void init();

  void destroy();

  void sendMessage(String text, String chatId);
}
