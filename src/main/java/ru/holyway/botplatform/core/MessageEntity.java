package ru.holyway.botplatform.core;

/**
 * Created by Sergey on 1/17/2017.
 */
public interface MessageEntity {

  String getText();

  String getSenderName();

  String getSenderLogin();

  String getChatId();

  void reply(String text);
}
