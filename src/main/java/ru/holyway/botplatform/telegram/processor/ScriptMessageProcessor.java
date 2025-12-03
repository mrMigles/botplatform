package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.scripting.*;
import ru.holyway.botplatform.scripting.entity.StaticMessage;
import ru.holyway.botplatform.scripting.entity.TimePredicate;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Component
@Order(99)
@Slf4j
public class ScriptMessageProcessor implements MessageProcessor, MessagePostLoader {

  private final TaskScheduler taskScheduler;
  private final ScriptCompiler scriptCompiler;
  private final DataHelper dataHelper;
  private final ScriptRegistry scriptRegistry;
  private final ScriptContextFactory scriptContextFactory;

  public ScriptMessageProcessor(ScriptCompiler scriptCompiler, DataHelper dataHelper,
                                @Qualifier("scriptScheduler") TaskScheduler taskScheduler) {
    this.scriptCompiler = scriptCompiler;
    this.dataHelper = dataHelper;
    this.taskScheduler = taskScheduler;
    this.scriptRegistry = new ScriptRegistry();
    this.scriptContextFactory = new ScriptContextFactory();

    loadStoredScripts();
  }

  private void loadStoredScripts() {
    dataHelper.getSettings().getScripts().forEach((chatId, storedScripts) -> storedScripts.forEach(storedScript -> {
      try {
        final Script script = scriptCompiler.compile(storedScript);
        script.setStringScript(storedScript);
        scriptRegistry.register(chatId, script);
      } catch (Exception e) {
        log.error("Error during loading script from storage for chat {}", chatId, e);
      }
    }));
  }

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return CollectionUtils.isNotEmpty(scriptRegistry.getScripts(messageEntity.getChatId())) || (
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
          String scriptString = originalScriptString.replaceAll("\\$", "\\\\\\$");
          scriptString = scriptString.replaceAll("\\.owner\\(\\d*\\)", "");
          scriptString += ".owner(" + messageEntity.getMessage().getFrom().getId() + ")";
          final Script script = scriptCompiler.compile(scriptString);
          script.setStringScript(scriptString);
          if (scriptRegistry.getScripts(messageEntity.getChatId()).contains(script)) {
            messageEntity.getSender()
                .execute(SendMessage.builder().text("Скрипт уже существует")
                    .chatId(messageEntity.getChatId())
                    .replyToMessageId(messageEntity.getMessage().getMessageId()).build());
            continue;
          }
          addTrigger(messageEntity.getSender(), messageEntity.getChatId(), script);
          scriptRegistry.register(messageEntity.getChatId(), script);
          dataHelper.getSettings().addScript(messageEntity.getChatId(), scriptString);
          dataHelper.updateSettings();

          sendScriptMenu(messageEntity, originalScriptString, script);
        }
        return;
      } catch (Exception e) {
        final String message = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
        final String formattedMessage = message.substring(0, Math.min(message.length(), 1000));
        messageEntity.getSender()
            .execute(SendMessage.builder().text("Ошибка компиляции: \n" + formattedMessage)
                .chatId(messageEntity.getChatId()).build());
        throw e;
      }
    }

    for (Script script : scriptRegistry.getScripts(messageEntity.getChatId())) {
      if (executeScript(messageEntity, script)) {
        return;
      }
    }
  }

  private boolean executeScript(TelegramMessageEntity messageEntity, Script script) {
    ScriptContext ctx = scriptContextFactory.create(messageEntity, script);
    final long start = System.currentTimeMillis();
    try {
      if (script.check(ctx)) {
        script.execute(ctx);
        if (script.isStopable()) {
          return true;
        }
      }
    } catch (Throwable e) {
      log.error("Error during execution script:", e);
      MetricCollector.getInstance().saveLog(messageEntity.getChatId(), script, e);
    } finally {
      MetricCollector.getInstance().trackCall(messageEntity.getChatId(), Math.toIntExact(System.currentTimeMillis() - start));
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

    keyboard.add(inlineKeyboardButtons);
    keyboardMarkup.setKeyboard(keyboard);

    int attempts = 0;
    while (attempts < 3) {
      try {
        return messageEntity.getSender()
            .execute(SendMessage.builder().chatId(messageEntity.getChatId())
                .text(scriptString)
                .replyMarkup(keyboardMarkup).build()).getMessageId();
      } catch (Throwable e) {
        log.warn("Attempt {} to send message and failed", attempts, e);
        attempts++;
        if (attempts == 3) {
          throw e;
        }
        try {
          Thread.sleep(250);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
    throw new RuntimeException("Should not reach here");
  }

  @Override
  public boolean isRegardingCallback(CallbackQuery callbackQuery) {
    return CollectionUtils.isNotEmpty(
        scriptRegistry.getScripts(callbackQuery.getMessage().getChatId().toString()));
  }

  @Override
  public void processCallBack(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {

    TelegramMessageEntity messageEntity = new TelegramMessageEntity(callbackQuery.getMessage(), callbackQuery, sender);

    for (Script script : scriptRegistry.getScripts(messageEntity.getChatId())) {
      if (executeScript(messageEntity, script)) {
        return;
      }
    }
  }

  public void clearScripts(final String chatId) {
    scriptRegistry.clear(chatId);
    dataHelper.getSettings().getScripts().remove(chatId);
    dataHelper.updateSettings();
  }

  public List<Script> getScripts(final String chatId) {
    return scriptRegistry.getScripts(chatId);
  }

  private void removeFromSettings(String chatId, Script script) {
    Optional.ofNullable(dataHelper.getSettings().getScripts().get(chatId))
        .ifPresent(list -> list.remove(script.getStringScript()));
    dataHelper.updateSettings();
  }

  public boolean removeScript(final String chatId, final String script) {
    Optional<Script> s = scriptRegistry.findByContent(chatId, script);
    s.ifPresent(foundScript -> {
      scriptRegistry.removeByContent(chatId, script);
      removeFromSettings(chatId, foundScript);
    });
    return s.isPresent();
  }

  public Script getScript(final String chatId, final String script) {
    return scriptRegistry.findByContent(chatId, script).orElse(null);
  }

  public boolean removeScript(final String chatId, final Integer scriptCode) {
    Optional<Script> s = scriptRegistry.findByHash(chatId, scriptCode);
    s.ifPresent(foundScript -> {
      scriptRegistry.removeByHash(chatId, scriptCode);
      removeFromSettings(chatId, foundScript);
    });
    return s.isPresent();
  }

  public Script getScript(final String chatId, final Integer scriptCode) {
    return scriptRegistry.findByHash(chatId, scriptCode).orElse(null);
  }

  @Override
  public void postRun(AbsSender absSender) {
    scriptRegistry.forEach((chat, chatScripts) -> chatScripts.forEach(script -> addTrigger(absSender, chat, script)));
  }

  private void addTrigger(AbsSender absSender, String chat, Script script) {
    if (script.getPredicates() != null && script.getPredicates() instanceof TimePredicate) {
      TimePredicate timePredicate = (TimePredicate) script.getPredicates();
      script.setTrigger(taskScheduler.schedule(() -> {
        Message message = new StaticMessage(chat);
        TelegramMessageEntity telegramMessageEntity = new TelegramMessageEntity(message,
            null, absSender);
        ScriptContext ctx = scriptContextFactory.create(telegramMessageEntity, script);
        final long start = System.currentTimeMillis();

        try {
          script.execute(ctx);
        } catch (Throwable e) {
          MetricCollector.getInstance().saveLog(chat, script, e);
          log.error("Error occurred during execution script: ", e);
        } finally {
          MetricCollector.getInstance().trackCall(chat, Math.toIntExact(System.currentTimeMillis() - start));
        }
      }, timePredicate.getTrigger()));
    }
  }
}
