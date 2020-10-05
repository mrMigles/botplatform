package ru.holyway.botplatform.scripting.entity;

import org.telegram.telegrambots.meta.api.objects.Message;

public class StaticMessage extends Message {

  private final String chatId;
  private Number messageId;

  public StaticMessage(String chatId) {
    this.chatId = chatId;
  }

  public StaticMessage(String chatId, Number messageId) {
    this.chatId = chatId;
    this.messageId = messageId;
  }

  @Override
  public Long getChatId() {
    return Long.valueOf(chatId);
  }

  @Override
  public Integer getMessageId() {
    return messageId != null ? Integer.valueOf(messageId.toString().replaceAll("\\.\\d*", "")) : null;
  }
}
