package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.scripting.Script;
import ru.holyway.botplatform.scripting.ScriptCompiler;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.TelegramScriptEntity;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.entity.StaticMessage;
import ru.holyway.botplatform.scripting.entity.TimePredicate;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.*;

@Component
@Order(99)
public class ScriptMessageProcessor implements MessageProcessor, MessagePostLoader {

  private final TaskScheduler taskScheduler;
  private ScriptCompiler scriptCompiler;
  private DataHelper dataHelper;
  private MultiValueMap<String, Script> scripts = new LinkedMultiValueMap<>();

  public ScriptMessageProcessor(ScriptCompiler scriptCompiler, DataHelper dataHelper,
                                @Qualifier("scriptScheduler") TaskScheduler taskScheduler) {
    this.scriptCompiler = scriptCompiler;
    this.dataHelper = dataHelper;
    this.taskScheduler = taskScheduler;

    for (Map.Entry<String, List<String>> chatScripts : dataHelper.getSettings().getScripts()
        .entrySet()) {
      for (String storedScript : chatScripts.getValue()) {
        try {
          final Script script = scriptCompiler.compile(storedScript);
          script.setStringScript(storedScript);
          scripts.add(chatScripts.getKey(), script);
        } catch (Exception e) {
          System.out.println(e.getMessage());
          e.printStackTrace();
        }
        scripts.get(chatScripts.getKey()).sort(Script::compareTo);
      }
    }
  }

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return CollectionUtils.isNotEmpty(scripts.get(messageEntity.getChatId())) || (
        messageEntity.getMessage().hasText() && messageEntity.getMessage().getText()
            .startsWith("script()"));
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    if (messageEntity.getMessage().hasText() && messageEntity.getMessage().getText()
        .startsWith("script()")) {
      try {
        String[] scriptStrings = messageEntity.getText().split(";\n");
        for (String originalScriptString : scriptStrings) {
          if (originalScriptString.isEmpty() || !originalScriptString.startsWith("script()")) {
            continue;
          }
          final String scriptString = originalScriptString.replaceAll("\\$", "\\\\\\$");
          final Script script = scriptCompiler.compile(scriptString);
          script.setStringScript(scriptString);
          if (scripts.getOrDefault(messageEntity.getChatId(), Collections.emptyList())
              .contains(script)) {
            messageEntity.getSender()
                .execute(new SendMessage().setText("Скрипт уже существует")
                    .setChatId(messageEntity.getChatId())
                    .setReplyToMessageId(messageEntity.getMessage().getMessageId()));
            continue;
          }
          addTrigger(messageEntity.getSender(), messageEntity.getChatId(), script);
          scripts.add(messageEntity.getChatId(), script);
          scripts.get(messageEntity.getChatId()).sort(Script::compareTo);
          dataHelper.getSettings().addScript(messageEntity.getChatId(), scriptString);
          dataHelper.updateSettings();

          sendScriptMenu(messageEntity, originalScriptString, script);
        }
        return;
      } catch (Exception e) {
        final String message = e.getMessage().substring(0, Math.min(e.getMessage().length(), 1000));
        messageEntity.getSender()
            .execute(new SendMessage().setText("Ошибка компиляции: \n" + message)
                .setChatId(messageEntity.getChatId()));
        throw e;
      }
    }

    for (Script script : scripts.get(messageEntity.getChatId())) {
      if (executeScript(messageEntity, script)) {
        return;
      }
    }
  }

  private boolean executeScript(TelegramMessageEntity messageEntity, Script script) {
    MessageScriptEntity message = new MessageScriptEntity(messageEntity);
    TelegramScriptEntity telegram = new TelegramScriptEntity();
    ScriptContext ctx = new ScriptContext(message, telegram);
    try {
      if (script.check(ctx)) {
        script.execute(ctx);
        if (script.isStopable()) {
          return true;
        }
      }
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
    }
    return false;
  }

  protected Integer sendScriptMenu(TelegramMessageEntity messageEntity, String scriptString,
                                Script script) throws TelegramApiException {
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
    inlineKeyboardButton.setText("Удалить");
    inlineKeyboardButton.setCallbackData("script:delete:" + script.hashCode());
    inlineKeyboardButtons.add(inlineKeyboardButton);

//        inlineKeyboardButton = new InlineKeyboardButton();
//        inlineKeyboardButton.setText("Остановить");
//        inlineKeyboardButton.setCallbackData("script:stop:" + script.hashCode());
//        inlineKeyboardButtons.add(inlineKeyboardButton);

    inlineKeyboardButton = new InlineKeyboardButton();
    inlineKeyboardButton.setText("Редактировать");
    inlineKeyboardButton.setCallbackData("script:edit:" + script.hashCode());
    inlineKeyboardButtons.add(inlineKeyboardButton);

    keyboard.add(inlineKeyboardButtons);
    keyboardMarkup.setKeyboard(keyboard);

    return messageEntity.getSender()
        .execute(new SendMessage().setChatId(messageEntity.getChatId())
            .setText(scriptString)
            .setReplyMarkup(keyboardMarkup)).getMessageId();
  }

  @Override
  public boolean isRegardingCallback(CallbackQuery callbackQuery) {
    return CollectionUtils.isNotEmpty(scripts.get(callbackQuery.getMessage().getChatId().toString()));
  }

  @Override
  public void processCallBack(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {

    TelegramMessageEntity messageEntity = new TelegramMessageEntity(callbackQuery.getMessage(), callbackQuery, sender);

    for (Script script : scripts.get(messageEntity.getChatId())) {
      if (executeScript(messageEntity, script)) {
        return;
      }
    }
  }

  public void clearScripts(final String chatId) {
    scripts.get(chatId).forEach(Script::cancel);
    scripts.remove(chatId);
    dataHelper.getSettings().getScripts().remove(chatId);
    dataHelper.updateSettings();
  }

  public List<Script> getScripts(final String chatId) {
    return scripts.getOrDefault(chatId, new ArrayList<>());
  }

  public boolean removeScript(final String chatId, final String script) {
    Optional<Script> s = scripts.get(chatId).stream()
        .filter(script1 -> script1.getStringScript().equals(script)).findFirst();
    if (s.isPresent()) {
      s.get().cancel();
      scripts.get(chatId).remove(s.get());
      dataHelper.getSettings().getScripts().get(chatId).remove(script);
      dataHelper.updateSettings();
      return true;
    }
    return false;
  }

  public boolean removeScript(final String chatId, final Integer scriptCode) {
    Optional<Script> s = scripts.get(chatId).stream()
        .filter(script -> script.hashCode() == scriptCode).findFirst();
    if (s.isPresent()) {
      s.get().cancel();
      scripts.get(chatId).remove(s.get());
      dataHelper.getSettings().getScripts().get(chatId).remove(s.get().getStringScript());
      dataHelper.updateSettings();
      return true;
    }
    return false;
  }

  @Override
  public void postRun(AbsSender absSender) {
    scripts.forEach((chat, chatScripts) -> {
      chatScripts.forEach(script -> {
        addTrigger(absSender, chat, script);
      });
    });
  }

  private void addTrigger(AbsSender absSender, String chat, Script script) {
    if (script.getPredicates() != null && script.getPredicates() instanceof TimePredicate) {
      TimePredicate timePredicate = (TimePredicate) script.getPredicates();
      script.setTrigger(taskScheduler.schedule(() -> {
        Message message = new StaticMessage(chat);
        TelegramMessageEntity telegramMessageEntity = new TelegramMessageEntity(message,
            null, absSender);
        ScriptContext ctx = new ScriptContext(new MessageScriptEntity(telegramMessageEntity), new TelegramScriptEntity());
        script.execute(ctx);
      }, timePredicate.getTrigger()));
    }
  }
}
