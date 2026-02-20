package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(2)
public class AnonymMessageProcessor implements MessageProcessor {

  private List<String> anonymChat = new ArrayList<>();

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    final String mes = messageEntity.getText();
    if (StringUtils.isNotEmpty(mes)) {
      if (mes.contains("/anon")) {
        return true;
      }
    }
    return anonymChat.contains(messageEntity.getChatId());
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    final String mes = messageEntity.getText();

    if (StringUtils.isNotEmpty(mes) && mes.contains("/anon")) {
      if (anonymChat.contains(messageEntity.getChatId())) {
        anonymChat.remove(messageEntity.getChatId());
        messageEntity.getSender()
            .execute(SendMessage.builder().text("Ok").chatId(messageEntity.getChatId()).build());
      } else {
        anonymChat.add(messageEntity.getChatId());
        messageEntity.getSender()
            .execute(SendMessage.builder().text("Ok").chatId(messageEntity.getChatId()).build());
      }
    } else {
      messageEntity.getSender().execute(DeleteMessage.builder().chatId(messageEntity.getChatId())
          .messageId(messageEntity.getMessage().getMessageId()).build());
      if (messageEntity.getMessage().getSticker() != null) {
        messageEntity.getSender().execute(
            SendMessage.builder().chatId(messageEntity.getChatId()).text("Кто-то прислал: \n").build());
        messageEntity.getSender().execute(
            SendSticker.builder().sticker(new InputFile(messageEntity.getMessage().getSticker().getFileId()))
                .chatId(messageEntity.getChatId()).build());
      } else if (messageEntity.getMessage().getCaption() != null) {
        messageEntity.getSender().execute(
            SendMessage.builder().chatId(messageEntity.getChatId()).text("Кто-то прислал: \n").build());
        messageEntity.getSender().execute(
            SendPhoto.builder().caption(messageEntity.getMessage().getCaption())
                .chatId(messageEntity.getChatId()).build());
      } else if (StringUtils.isNotEmpty(mes)) {
        messageEntity.getSender().execute(SendMessage.builder().chatId(messageEntity.getChatId())
            .text("Кто-то сказал: \n" + mes).build());
      }
    }
  }

  @Override
  public boolean isRegardingCallback(CallbackQuery callbackQuery) {
    return false;
  }

  @Override
  public void processCallBack(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {

  }
}
