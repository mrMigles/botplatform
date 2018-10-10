package ru.holyway.botplatform.telegram.processor;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

public interface MessageProcessor {

  /**
   * @param messageEntity
   * @return
   */
  boolean isNeedToHandle(final TelegramMessageEntity messageEntity);

  /**
   * @param messageEntity
   */
  void process(final TelegramMessageEntity messageEntity) throws TelegramApiException;

  /**
   * @param callbackQuery
   * @return
   */
  boolean isRegardingCallback(final CallbackQuery callbackQuery);

  /**
   * @param callbackQuery
   * @param sender
   */
  void processCallBack(final CallbackQuery callbackQuery, final AbsSender sender)
      throws TelegramApiException;

}
