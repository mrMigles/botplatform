package ru.holyway.botplatform.scripting;

import java.util.function.Consumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;

public class TelegramScriptEntity {

  private AbsSender sender;

  public TelegramScriptEntity(AbsSender sender) {
    this.sender = sender;
  }

  public TelegramScriptEntity() {

  }

  public Consumer<MessageScriptEntity> send(String chatId, String text) {
    return s -> {
      try {
        sender
            .execute(new SendMessage().setText(text).setChatId(chatId));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<MessageScriptEntity> send(Long chatId, String text) {
    return s -> {
      try {
        sender
            .execute(new SendMessage().setText(text).setChatId(chatId));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }
}
