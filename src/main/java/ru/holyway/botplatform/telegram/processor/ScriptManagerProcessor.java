package ru.holyway.botplatform.telegram.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.Script;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(98)
public class ScriptManagerProcessor implements MessageProcessor {

  @Autowired
  private ScriptMessageProcessor scriptMessageProcessor;

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return messageEntity.getMessage().hasText() && (messageEntity.getMessage().getText()
        .startsWith("/clear") || messageEntity.getMessage().getText()
        .startsWith("/list") || (messageEntity.getMessage().isReply() && messageEntity
        .getMessage().getReplyToMessage().getText().startsWith("script()") && (messageEntity
        .getMessage().getText().startsWith("/remove") || messageEntity
        .getMessage().getText().equals("-"))));
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    if (messageEntity.getMessage().getText()
        .startsWith("/clear")) {
      scriptMessageProcessor.clearScripts(messageEntity.getChatId());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Cleared"));
    } else if (messageEntity.getMessage().getText()
        .startsWith("/list")) {
      messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId())
          .setText("List of scripts:"));
      for (Script script : scriptMessageProcessor.getScripts(messageEntity.getChatId())) {
        messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId())
            .setText(script.getStringScript()));
      }
    } else if (messageEntity.getMessage().getText()
        .startsWith("/remove") || messageEntity.getMessage().getText()
        .equals("-")) {
      final String script = messageEntity.getMessage().getReplyToMessage().getText();
      scriptMessageProcessor.removeScript(messageEntity.getChatId(), script);
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Removed"));
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
