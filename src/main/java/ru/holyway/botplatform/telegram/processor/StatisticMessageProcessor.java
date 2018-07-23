package ru.holyway.botplatform.telegram.processor;

import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

public class StatisticMessageProcessor implements MessageProcessor {

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return !messageEntity.getMessage().getFrom().getBot();
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {

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
