package ru.holyway.botplatform.telegram.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(98)
public class ScriptManagerProcessor implements MessageProcessor {

  @Autowired
  private ScriptMessageProcessor scriptMessageProcessor;

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return messageEntity.getMessage().hasText() && messageEntity.getMessage().getText()
        .startsWith("/clear_scripts");
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    scriptMessageProcessor.clearScripts(messageEntity.getChatId());
    messageEntity.getSender()
        .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Cleared"));
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
