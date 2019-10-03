package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
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
        .getMessage().getReplyToMessage().getText().startsWith("script()")));
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    if (messageEntity.getMessage().getText()
        .startsWith("/clear")) {
      scriptMessageProcessor.clearScripts(messageEntity.getChatId());
      messageEntity.getSender()
          .execute(
              new SendMessage().setChatId(messageEntity.getChatId()).setText("Скрипты очищены"));
    } else if (messageEntity.getMessage().getText()
        .startsWith("/list")) {
      messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId())
          .setText("Список скриптов:"));
      for (Script script : scriptMessageProcessor.getScripts(messageEntity.getChatId())) {
        scriptMessageProcessor.sendScriptMenu(messageEntity, script.getStringScript(), script);
      }
    } else if (messageEntity.getMessage().getText()
        .startsWith("/remove") || messageEntity.getMessage().getText()
        .equals("-")) {
      final String script = messageEntity.getMessage().getReplyToMessage().getText();
      if (scriptMessageProcessor.removeScript(messageEntity.getChatId(), script)) {
        messageEntity.getSender()
            .execute(
                new SendMessage().setChatId(messageEntity.getChatId()).setText("Скрипт удален"));
      } else {
        messageEntity.getSender()
            .execute(
                new SendMessage().setChatId(messageEntity.getChatId()).setText("Скрипт не найден"));
      }
    } else if (messageEntity.getMessage().isReply() && messageEntity.getMessage().getText()
        .startsWith("script(")) {
      if (scriptMessageProcessor
          .removeScript(messageEntity.getChatId(),
              messageEntity.getMessage().getReplyToMessage().getText())) {
        messageEntity.getSender()
            .execute(
                new SendMessage().setChatId(messageEntity.getChatId()).setText("Скрипт изменен")
                    .setReplyToMessageId(messageEntity.getMessage().getMessageId()));
        scriptMessageProcessor.process(messageEntity);
      } else {
        messageEntity.getSender()
            .execute(
                new SendMessage().setChatId(messageEntity.getChatId()).setText("Скрипт не найден"));
      }
//      messageEntity.getSender().execute(new DeleteMessage().setChatId(messageEntity.getChatId())
//          .setMessageId(messageEntity.getMessage().getReplyToMessage().getMessageId()));
    }
  }

  @Override
  public boolean isRegardingCallback(CallbackQuery callbackQuery) {
    return callbackQuery.getData().startsWith("script:");
  }

  @Override
  public void processCallBack(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {
    if (callbackQuery.getData().startsWith("script:edit:")) {
      final String scriptId = StringUtils.substringAfter(callbackQuery.getData(), "script:edit:");
      sender
          .execute(new SendMessage().setReplyMarkup(new ForceReplyKeyboard())
              .setChatId(callbackQuery.getMessage().getChatId())
              .setText(callbackQuery.getMessage().getText()));
    } else if (callbackQuery.getData().startsWith("script:delete:")) {
      final String scriptId = StringUtils.substringAfter(callbackQuery.getData(), "script:delete:");
      if (scriptMessageProcessor.removeScript(
          String.valueOf(callbackQuery.getMessage().getChatId()), Integer.valueOf(scriptId))) {
        sender
            .execute(
                new SendMessage().setChatId(callbackQuery.getMessage().getChatId())
                    .setText("Скрипт удален")
                    .setReplyToMessageId(callbackQuery.getMessage().getMessageId()));
      } else {
        sender.execute(new AnswerCallbackQuery().setCallbackQueryId(callbackQuery.getId())
            .setText("Скрипт не найден"));
      }
    }
  }
}
