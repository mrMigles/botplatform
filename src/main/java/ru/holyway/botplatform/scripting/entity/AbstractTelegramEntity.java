package ru.holyway.botplatform.scripting.entity;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.ScriptContext;

public abstract class AbstractTelegramEntity {

  public abstract Function<ScriptContext, Message> entity();

  public Predicate<ScriptContext> hasSticker(String text) {
    return mes -> entity().apply(mes).getSticker().getFileId().equals(text);
  }

  public Predicate<ScriptContext> hasSticker() {
    return mes -> entity().apply(mes).hasSticker();
  }

  public Predicate<ScriptContext> isReply() {
    return mes -> entity().apply(mes).isReply();
  }

  public Predicate<ScriptContext> isForward() {
    return mes -> entity().apply(mes).getForwardFromMessageId() != null;
  }

  public Consumer<ScriptContext> send(String text) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(
                new SendMessage().setText(text).setChatId(entity().apply(s).getChatId()));
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
                    .setChatId(entity().apply(s).getChatId()));
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
                    .setReplyToMessageId(entity().apply(s).getMessageId())
                    .setText(text).setChatId(entity().apply(s).getChatId()));
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
                .setMessageId(entity().apply(s).getMessageId())
                .setChatId(entity().apply(s).getChatId()));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

}
