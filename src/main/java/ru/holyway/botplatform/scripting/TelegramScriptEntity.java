package ru.holyway.botplatform.scripting;

import java.util.function.Consumer;
import java.util.function.Function;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramScriptEntity {

  public TelegramScriptEntity() {

  }

  private Consumer<ScriptContext> send(String chatId, String text) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(SendMessage.builder().text(text).chatId(chatId).build());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  private Consumer<ScriptContext> send(Long chatId, String text) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(SendMessage.builder().text(text).chatId(String.valueOf(chatId)).build());
      } catch (TelegramApiException e) {
        e.printStackTrace();
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
