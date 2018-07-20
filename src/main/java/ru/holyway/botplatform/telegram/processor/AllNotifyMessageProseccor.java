package ru.holyway.botplatform.telegram.processor;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(3)
public class AllNotifyMessageProseccor implements MessageProcessor {

  @Autowired
  DataHelper dataHelper;

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return StringUtils.containsIgnoreCase(messageEntity.getText(), "@all") || StringUtils
        .containsIgnoreCase(messageEntity.getText(), "/all");
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    final List<String> users = new ArrayList<>();
    dataHelper.getChatMembers(messageEntity.getChatId()).forEach(userId -> {
      try {
        User user = messageEntity.getSender().execute(new GetChatMember().setUserId(
            Integer.valueOf(userId)).setChatId(messageEntity.getChatId())).getUser();
        String nameOfUser = user.getUserName();
        if (StringUtils.isEmpty(nameOfUser)) {
          nameOfUser = user.getFirstName() + " " + user.getLastName();
        }
        users.add("@" + nameOfUser);
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    });
    if (!users.isEmpty()) {
      messageEntity.getSender().execute(
          new SendMessage().setReplyToMessageId(messageEntity.getMessage().getMessageId())
              .setText(String.join(", ", users)).setChatId(messageEntity.getChatId()));
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
