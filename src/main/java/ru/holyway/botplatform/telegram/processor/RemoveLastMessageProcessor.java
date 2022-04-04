package ru.holyway.botplatform.telegram.processor;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(2)
public class RemoveLastMessageProcessor implements MessageProcessor {

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    final String mes = messageEntity.getText();
    if (StringUtils.isNotEmpty(mes) && StringUtils.equalsIgnoreCase(mes, "удали")) {
      if (messageEntity.getMessage().getReplyToMessage() != null) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    try {
      final Message replyMessage = messageEntity.getMessage().getReplyToMessage();
      if (hasGrants(messageEntity)) {
        for (int i = replyMessage.getMessageId(); i <= messageEntity.getMessage().getMessageId();
            i++) {
          try {
            messageEntity.getSender()
                .execute(DeleteMessage.builder().chatId(messageEntity.getChatId()).messageId(i).build());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }

    } catch (Exception e) {
      messageEntity.getSender()
          .execute(SendMessage.builder().chatId(messageEntity.getChatId()).text("Нимагу").build());
      e.printStackTrace();
    }
  }

  private boolean hasGrants(TelegramMessageEntity messageEntity) throws TelegramApiException {
    List<ChatMember> chatMembers = messageEntity.getSender()
        .execute(GetChatAdministrators.builder().chatId(messageEntity.getChatId()).build());
    for (ChatMember chatMember : chatMembers) {
      if (chatMember.getUser().getId().equals(messageEntity.getMessage().getFrom().getId())) {
        if (chatMember.getStatus().equals("creator") || chatMember.getUser().getCanReadAllGroupMessages()) {
          return true;
        }
      }
    }
    return false;
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
