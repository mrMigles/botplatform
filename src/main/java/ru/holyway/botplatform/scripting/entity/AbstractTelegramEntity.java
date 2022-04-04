package ru.holyway.botplatform.scripting.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.stickers.GetStickerSet;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.TextJoiner;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractTelegramEntity {

  public abstract Function<ScriptContext, Message> entity();

  private ObjectMapper mapper = new ObjectMapper();

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
    return mes -> entity().apply(mes).getForwardFrom() != null ||
        entity().apply(mes).getForwardSenderName() != null;
  }

  public Consumer<ScriptContext> send(String text) {
    return s -> {
      try {
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(
                SendMessage.builder().text(text).chatId(String.valueOf(entity().apply(s).getChatId())).build())
            .getMessageId().toString());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> sendMessage(Function<ScriptContext, SendMessage> messageFunction) {
    return s -> {
      try {
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(messageFunction.apply(s))
            .getMessageId().toString());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> sendSticker(String fileId) {
    return s -> {
      try {
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(
                SendSticker.builder().sticker(new InputFile(fileId))
                    .chatId(String.valueOf(entity().apply(s).getChatId())).build()).getMessageId().toString());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> sendStickerByEmoji(String stickerSet, String emoji) {
    return s -> {
      try {
        List<Sticker> stickers = s.message.messageEntity.getSender()
            .execute(GetStickerSet.builder().name(stickerSet).build()).getStickers();
        Sticker foundSticker = stickers.stream()
            .filter(sticker -> StringUtils.startsWithIgnoreCase(sticker.getEmoji(), emoji)).findFirst()
            .orElseThrow(TelegramApiException::new);
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(
                SendSticker.builder().sticker(new InputFile(foundSticker.getFileId()))
                    .chatId(String.valueOf(entity().apply(s).getChatId())).build()).getMessageId().toString());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> sendStickerFromSet(String stickerSet) {
    return s -> {
      try {
        List<Sticker> stickers = s.message.messageEntity.getSender()
            .execute(GetStickerSet.builder().name(stickerSet).build()).getStickers();
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(
                SendSticker.builder()
                    .sticker(new InputFile(stickers.get(new Random().nextInt(stickers.size())).getFileId()))
                    .chatId(String.valueOf(entity().apply(s).getChatId())).build()).getMessageId().toString());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> sendStickerFromSet(Function<ScriptContext, String> supplierText) {
    return s -> sendStickerFromSet(supplierText.apply(s)).accept(s);
  }

  public Consumer<ScriptContext> sendMedia(String url) {
    return s -> {
      try {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(String.valueOf(entity().apply(s).getChatId()));
        sendMediaGroup.setMedias(
            Collections.singletonList(new InputMediaPhoto(url)));
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
        SendPhoto.SendPhotoBuilder sendMediaGroup = SendPhoto.builder()
            .chatId(String.valueOf(entity().apply(s).getChatId()));
        sendMediaGroup.photo(new InputFile(url));
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(sendMediaGroup.build()).getMessageId().toString());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> sendVideo(String url) {
    return s -> {
      try {
        SendVideo sendMediaGroup = SendVideo.builder()
            .chatId(String.valueOf(entity().apply(s).getChatId())).build();
        sendMediaGroup.setVideo(new InputFile(url));
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(sendMediaGroup).getMessageId().toString());
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
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(
                SendMessage.builder()
                    .replyToMessageId(entity().apply(s).getMessageId())
                    .text(text).chatId(String.valueOf(entity().apply(s).getChatId())).build()).getMessageId()
            .toString());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> edit(Function<ScriptContext, SendMessage> message) {
    return s -> {
      try {
        SendMessage sendMessage = message.apply(s);

        if (StringUtils.isNotEmpty(sendMessage.getText())) {
          s.message.messageEntity.getSender()
              .execute(
                  EditMessageText.builder()
                      .messageId(entity().apply(s).getMessageId())
                      .chatId(String.valueOf(entity().apply(s).getChatId()))
                      .text(sendMessage.getText())
                      .replyMarkup(entity().apply(s).getReplyMarkup()).build()
              );
        }

        if (entity().apply(s).getReplyMarkup() != null && sendMessage.getReplyMarkup() != null && !sendMessage.getReplyMarkup().equals(entity().apply(s).getReplyMarkup())) {
          s.message.messageEntity.getSender()
              .execute(
                  EditMessageReplyMarkup.builder()
                      .messageId(entity().apply(s).getMessageId())
                      .chatId(String.valueOf(entity().apply(s).getChatId()))
                      .replyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup()).build()
              );
        }
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> forward(String chatId) {
    return forward();
  }

  public Consumer<ScriptContext> forward(Long chatId) {
    return forward();
  }

  public Consumer<ScriptContext> forward() {
    return s -> {
      try {
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(ForwardMessage.builder().messageId(entity().apply(s).getMessageId())
                .chatId(s.message.messageEntity.getChatId()).fromChatId(String.valueOf(entity().apply(s).getChatId())).build()).getMessageId()
            .toString());
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
            .execute(DeleteMessage.builder()
                .messageId(entity().apply(s).getMessageId())
                .chatId(String.valueOf(entity().apply(s).getChatId())).build());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public TextJoiner getChatId() {
    return TextJoiner.text(scriptContext -> entity().apply(scriptContext).getChatId().toString());
  }

  public TextJoiner getId() {
    return TextJoiner.text(scriptContext -> entity().apply(scriptContext).getMessageId().toString());
  }

  public TextJoiner json = TextJoiner.text(scriptContext -> {
    try {
      return mapper.writeValueAsString(entity().apply(scriptContext));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  });

  public ChatTelegramEntity chat() {
    return new ChatTelegramEntity(getChatId());
  }

  public TextJoiner last() {
    return TextJoiner.text(scriptContext -> scriptContext.getContextValue("lastMessage"));
  }

}
