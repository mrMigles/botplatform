package ru.holyway.botplatform.scripting.entity;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

public class MessageScriptEntity {

  public TelegramMessageEntity messageEntity;

  public TextScriptEntity text = new TextScriptEntity();

  public UserScriptEntity user = new UserScriptEntity();

  public MessageScriptEntity() {
  }

  public MessageScriptEntity(TelegramMessageEntity messageEntity) {
    this.messageEntity = messageEntity;
  }

  public Predicate<ScriptContext> hasSticker(String text) {
    return mes -> mes.message.messageEntity.getMessage().getSticker().getFileId().equals(text);
  }

  public Predicate<ScriptContext> hasSticker() {
    return mes -> mes.message.messageEntity.getMessage().hasSticker();
  }

  public Consumer<ScriptContext> send(String text) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(
                new SendMessage().setText(text).setChatId(s.message.messageEntity.getChatId()));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> sendSticker(String fileId) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(
                new SendSticker().setSticker(fileId)
                    .setChatId(s.message.messageEntity.getChatId()));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> send(Function<ScriptContext, String> supplierText) {
    return s -> send(supplierText.apply(s)).accept(s);
  }

  public Consumer<ScriptContext> reply(String text) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(
                new SendMessage()
                    .setReplyToMessageId(s.message.messageEntity.getMessage().getMessageId())
                    .setText(text).setChatId(s.message.messageEntity.getChatId()));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> reply(Function<ScriptContext, String> supplierText) {
    return s -> reply(supplierText.apply(s)).accept(s);
  }

  public Consumer<ScriptContext> delete() {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(new DeleteMessage()
                .setMessageId(s.message.messageEntity.getMessage().getMessageId())
                .setChatId(s.message.messageEntity.getChatId()));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

}
