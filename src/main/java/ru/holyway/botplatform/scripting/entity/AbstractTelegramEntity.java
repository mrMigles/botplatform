package ru.holyway.botplatform.scripting.entity;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
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

  public Consumer<ScriptContext> sendMedia(String url) {
    return s -> {
      try {
        SendMediaGroup sendMediaGroup = new SendMediaGroup()
            .setChatId(entity().apply(s).getChatId());
        sendMediaGroup.setMedia(
            Collections.singletonList(new InputMediaPhoto(url, "")));
        s.message.messageEntity.getSender()
            .execute(sendMediaGroup);
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> sendPhoto(String url) {
    return s -> {
      try {
        SendPhoto sendMediaGroup = new SendPhoto()
            .setChatId(entity().apply(s).getChatId());
        sendMediaGroup.setPhoto(url);
        s.message.messageEntity.getSender()
            .execute(sendMediaGroup);
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> sendVideo(String url) {
    return s -> {
      try {
        SendVideo sendMediaGroup = new SendVideo()
            .setChatId(entity().apply(s).getChatId());
        sendMediaGroup.setVideo(url);
        s.message.messageEntity.getSender()
            .execute(sendMediaGroup);
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

  public Consumer<ScriptContext> forward(String chatId) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(new ForwardMessage().setMessageId(entity().apply(s).getMessageId())
                .setChatId(chatId).setFromChatId(entity().apply(s).getChatId()));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> forward(Long chatId) {
    return s -> {
      try {
        s.message.messageEntity.getSender()
            .execute(new ForwardMessage().setMessageId(entity().apply(s).getMessageId())
                .setChatId(chatId).setFromChatId(entity().apply(s).getChatId()));
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
