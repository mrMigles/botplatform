package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(3)
public class AllNotifyMessageProseccor implements MessageProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AllNotifyMessageProseccor.class);

  @Autowired
  DataHelper dataHelper;

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return StringUtils.containsIgnoreCase(messageEntity.getText(), "@all");
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    final List<String> users = new ArrayList<>();
    dataHelper.getChatMembers(messageEntity.getChatId()).forEach(userId -> {
      try {
        User user = messageEntity.getSender().execute(GetChatMember.builder().userId(
            Long.valueOf(userId)).chatId(messageEntity.getChatId()).build()).getUser();
        String nameOfUser = user.getUserName();
        if (StringUtils.isEmpty(nameOfUser)) {
          nameOfUser = user.getFirstName() + " " + user.getLastName();
        }
        users.add("@" + nameOfUser);
      } catch (TelegramApiException e) {
        LOGGER.error("Error fetching chat member {}", userId, e);
      }
    });
    if (!users.isEmpty()) {
      messageEntity.getSender().execute(
          SendMessage.builder().replyToMessageId(messageEntity.getMessage().getMessageId())
              .text(String.join(", ", users)).chatId(messageEntity.getChatId()).build());
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
