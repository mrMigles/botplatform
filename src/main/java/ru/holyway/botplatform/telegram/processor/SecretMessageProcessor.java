package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(1)
public class SecretMessageProcessor implements MessageProcessor {

  private Map<String, List<Integer>> secretMessages = new HashMap<>();

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    final String mes = messageEntity.getText();
    if (StringUtils.isNotEmpty(mes)) {
      if (mes.contains("/secret")) {
        return true;
      }
    }
    return secretMessages.get(messageEntity.getChatId()) != null;
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    List<Integer> messages = secretMessages.get(messageEntity.getChatId());
    if (messages != null) {
      if (StringUtils.isNotEmpty(messageEntity.getText()) && StringUtils
          .containsIgnoreCase(messageEntity.getText(), "/secret")) {
        messages.add(messageEntity.getMessage().getMessageId());
        for (Integer integer : messages) {
          try {
            messageEntity.getSender().execute(
                DeleteMessage.builder().chatId(messageEntity.getChatId()).messageId(integer).build());
          } catch (Exception e) {
            e.printStackTrace();
          }
          secretMessages.remove(messageEntity.getChatId());
        }
      } else {
        messages.add(messageEntity.getMessage().getMessageId());
      }
    } else {
      if (StringUtils.isNotEmpty(messageEntity.getText()) && StringUtils
          .containsIgnoreCase(messageEntity.getText(), "/secret")) {
        messages = new ArrayList<>();
        messages.add(messageEntity.getMessage().getMessageId());
        Message message = messageEntity.getSender().execute(
            SendMessage.builder().chatId(messageEntity.getChatId())
                .text("Ах вы шалушники, так и придётся скрыть ваши разговоры.").build());
        messages.add(message.getMessageId());
        secretMessages.put(messageEntity.getChatId(), messages);
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
