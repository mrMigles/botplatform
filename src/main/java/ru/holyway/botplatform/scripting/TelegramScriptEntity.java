package ru.holyway.botplatform.scripting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.Consumer;
import java.util.function.Function;

public class TelegramScriptEntity {

  private static final Logger LOGGER = LoggerFactory.getLogger(TelegramScriptEntity.class);

  public TelegramScriptEntity() {

  }

  private Consumer<ScriptContext> send(String chatId, String text) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(SendMessage.builder().text(text).chatId(chatId).build());
      } catch (TelegramApiException e) {
        LOGGER.error("Error sending message to chat {}", chatId, e);
      }
    };
  }

  private Consumer<ScriptContext> send(Long chatId, String text) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(SendMessage.builder().text(text).chatId(String.valueOf(chatId)).build());
      } catch (TelegramApiException e) {
        LOGGER.error("Error sending message to chat {}", chatId, e);
      }
    };
  }

  private Consumer<ScriptContext> send(Long chatId, Function<ScriptContext, String> supplierText) {
    return s -> send(chatId, supplierText.apply(s)).accept(s);
  }

  private Consumer<ScriptContext> send(String chatId, Function<ScriptContext, String> supplierText) {
    return s -> send(chatId, supplierText.apply(s)).accept(s);
  }
}
