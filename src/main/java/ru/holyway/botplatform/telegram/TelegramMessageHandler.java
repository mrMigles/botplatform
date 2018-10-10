package ru.holyway.botplatform.telegram;

import ru.holyway.botplatform.core.CommonMessageHandler;
import ru.holyway.botplatform.core.MessageEntity;

/**
 * Created by Sergey on 1/17/2017.
 */
public class TelegramMessageHandler extends CommonMessageHandler {

  @Override
  protected void sendMessageInternal(MessageEntity messageEntity, String text) {
    messageEntity.reply(text);
  }
}
