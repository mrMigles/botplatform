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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.MetricCollector;
import ru.holyway.botplatform.scripting.Script;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Order(98)
public class ScriptManagerProcessor implements MessageProcessor {

  public static final String SECURITY_VALUE_SET_REGEX = "(\\/put)(\\s)(\\\")(.*)(\\\")(\\s)(\\\")(.*)(\\\")";
  @Autowired
  private ScriptMessageProcessor scriptMessageProcessor;

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return messageEntity.getMessage().hasText() && (messageEntity.getMessage().getText()
        .startsWith("/clear") || messageEntity.getMessage().getText()
        .startsWith("/list") || messageEntity.getMessage().getText()
        .startsWith("/logs") || messageEntity.getMessage().getText().matches(SECURITY_VALUE_SET_REGEX) || messageEntity.getMessage().getText()
        .startsWith("/get")
        || (messageEntity.getMessage().isReply() && messageEntity
        .getMessage().getReplyToMessage().hasText() && messageEntity
        .getMessage().getReplyToMessage().getText().startsWith("script()")));
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    if (messageEntity.getMessage().getText()
        .startsWith("/clear")) {
      scriptMessageProcessor.clearScripts(messageEntity.getChatId());
      messageEntity.getSender()
          .execute(
              SendMessage.builder().chatId(messageEntity.getChatId()).text("Скрипты очищены").build());
    } else if (messageEntity.getMessage().getText()
        .startsWith("/list")) {
      Integer firstId = messageEntity.getSender().execute(SendMessage.builder().chatId(messageEntity.getChatId())
          .text("Список скриптов:").build()).getMessageId();
      List<Integer> messages = new ArrayList<>();
      List<Script> scripts = scriptMessageProcessor.getScripts(messageEntity.getChatId());
      for (int i = 0; i < (Math.min(scripts.size(), 8)); i++) {
        Script script = scripts.get(i);
        messages.add(scriptMessageProcessor.sendScriptMenu(messageEntity, script.getStringScript().replaceAll("\\\\\\$", "\\$").replaceAll("\\.owner\\(\\d*\\)", ""), script));
      }
      sendControlButtons(messageEntity, Math.min(scripts.size(), 8), firstId);
    } else if (messageEntity.getMessage().getText()
        .startsWith("/remove") || messageEntity.getMessage().getText()
        .equals("-")) {
      final String script = messageEntity.getMessage().getReplyToMessage().getText();
      if (scriptMessageProcessor.removeScript(messageEntity.getChatId(), script)) {
        messageEntity.getSender()
            .execute(
                SendMessage.builder().chatId(messageEntity.getChatId()).text("Скрипт удален").build());
      } else {
        messageEntity.getSender()
            .execute(
                SendMessage.builder().chatId(messageEntity.getChatId()).text("Скрипт не найден").build());
      }
    } else if (messageEntity.getMessage().getText().matches(SECURITY_VALUE_SET_REGEX)) {
      Pattern pattern = Pattern.compile(SECURITY_VALUE_SET_REGEX);

      Matcher m = pattern.matcher(messageEntity.getText());
      m.find();
      String key = m.group(4);
      String value = m.group(8);
      if (value.isEmpty()) {
        value = null;
      }
      scriptMessageProcessor.dataHelper.putToSecretStorage(messageEntity.getChatId(), key, value);
      messageEntity.getSender()
          .execute(
              SendMessage.builder().chatId(messageEntity.getChatId()).text("Сохранено: secret[" + key + "]=" + value).build());
    } else if (messageEntity.getMessage().getText()
        .startsWith("/logs")) {

      InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

      List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

      List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

      InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
      inlineKeyboardButton.setText("Скрыть лог");
      inlineKeyboardButton.setCallbackData("script:clear:" + messageEntity.getMessage().getMessageId());
      inlineKeyboardButtons.add(inlineKeyboardButton);

      keyboard.add(inlineKeyboardButtons);
      keyboardMarkup.setKeyboard(keyboard);

      messageEntity.getSender()
          .execute(
              SendMessage.builder().chatId(messageEntity.getChatId()).text(MetricCollector.getInstance().getLog(messageEntity.getChatId())).replyMarkup(keyboardMarkup).build());
    } else if (messageEntity.getMessage().getText()
        .startsWith("/get")) {
      messageEntity.getSender()
          .execute(
              SendMessage.builder().chatId(messageEntity.getChatId()).text(scriptMessageProcessor.dataHelper.listSecretStorage(messageEntity.getChatId()).toString()).build());
    } else if (messageEntity.getMessage().isReply() && messageEntity.getMessage().getText()
        .startsWith("script(")) {
      if (scriptMessageProcessor
          .removeScript(messageEntity.getChatId(),
              messageEntity.getMessage().getReplyToMessage().getText())) {
        messageEntity.getSender()
            .execute(
                SendMessage.builder().chatId(messageEntity.getChatId()).text("Скрипт изменен")
                    .replyToMessageId(messageEntity.getMessage().getMessageId()).build());
        scriptMessageProcessor.process(messageEntity);
      } else {
        messageEntity.getSender()
            .execute(
                SendMessage.builder().chatId(messageEntity.getChatId()).text("Скрипт не найден").build());
      }
//      messageEntity.getSender().execute(new DeleteMessage().chatId(messageEntity.getChatId())
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
          .execute(SendMessage.builder().replyMarkup(new ForceReplyKeyboard())
              .chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
              .text(callbackQuery.getMessage().getText()).build());
    } else if (callbackQuery.getData().startsWith("script:delete:")) {
      final String scriptId = StringUtils.substringAfter(callbackQuery.getData(), "script:delete:");
      if (scriptMessageProcessor.removeScript(
          String.valueOf(callbackQuery.getMessage().getChatId()), Integer.valueOf(scriptId))) {
        sender
            .execute(
                SendMessage.builder().chatId(String.valueOf(callbackQuery.getMessage().getChatId()))
                    .text("Скрипт удален")
                    .replyToMessageId(callbackQuery.getMessage().getMessageId()).build());
      } else {
        sender.execute(AnswerCallbackQuery.builder().callbackQueryId(callbackQuery.getId())
            .text("Скрипт не найден").build());
      }
    } else if (callbackQuery.getData().startsWith("script:clear:")) {
      final int firstMessage = Integer.parseInt(StringUtils.substringAfterLast(callbackQuery.getData(), ":"));
      for (int i = firstMessage; i <= callbackQuery.getMessage().getMessageId(); i++) {
        try {
          sender.execute(DeleteMessage.builder().chatId(String.valueOf(callbackQuery.getMessage().getChatId())).messageId(i).build());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else if (callbackQuery.getData().startsWith("script:more:")) {
      final int firstMessage = Integer.parseInt(StringUtils.substringAfterLast(callbackQuery.getData(), ":"));
      final int offset = Integer.parseInt(StringUtils.substringBetween(callbackQuery.getData(), "script:more:", ":"));
      List<Script> scripts = scriptMessageProcessor.getScripts(callbackQuery.getMessage().getChatId().toString());
      int max = Math.min(scripts.size(), offset + 8);
      if (max == offset) {
        return;
      }
      sender.execute(DeleteMessage.builder().chatId(String.valueOf(callbackQuery.getMessage().getChatId())).messageId(callbackQuery.getMessage().getMessageId()).build());
      TelegramMessageEntity messageEntity = new TelegramMessageEntity(callbackQuery.getMessage(), callbackQuery, sender);
      for (int i = offset; i < max; i++) {
        Script script = scripts.get(i);
        try {
          scriptMessageProcessor.sendScriptMenu(messageEntity, script.getStringScript().replaceAll("\\\\\\$", "\\$").replaceAll("\\.owner\\(\\d*\\)", ""), script);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      sendControlButtons(messageEntity, max, firstMessage);
    }
  }

  protected Integer sendControlButtons(TelegramMessageEntity messageEntity, Integer offset, Integer first) throws TelegramApiException {
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
    inlineKeyboardButton.setText("Удалить вывод");
    inlineKeyboardButton.setCallbackData("script:clear:" + first);
    inlineKeyboardButtons.add(inlineKeyboardButton);

    inlineKeyboardButton = new InlineKeyboardButton();
    inlineKeyboardButton.setText("Вывести ещё");
    inlineKeyboardButton.setCallbackData("script:more:" + offset + ":" + first);
    inlineKeyboardButtons.add(inlineKeyboardButton);

    keyboard.add(inlineKeyboardButtons);
    keyboardMarkup.setKeyboard(keyboard);

    return messageEntity.getSender()
        .execute(SendMessage.builder().chatId(messageEntity.getChatId())
            .text("В списке ещё " + (scriptMessageProcessor.getScripts(messageEntity.getChatId()).size() - offset) + " скриптов")
            .replyMarkup(keyboardMarkup).build()).getMessageId();
  }
}
