package ru.holyway.botplatform.telegram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.core.CommonHandler;
import ru.holyway.botplatform.telegram.processor.MessagePostLoader;
import ru.holyway.botplatform.telegram.processor.MessageProcessor;

/**
 * Created by Sergey on 1/17/2017.
 */
public class TelegramBot extends TelegramLongPollingBot implements Bot {

  @Value("${credential.telegram.login}")
  private String botName;

  @Value("${credential.telegram.token}")
  private String botToken;

  @Autowired
  private List<MessageProcessor> messageProcessors;

  @Autowired
  private List<MessagePostLoader> messagePostLoaders;

  @Autowired
  private CommonHandler commonMessageHandler;

  private Map<Integer, String> locations = new HashMap<>();

  private Map<Integer, String> realAddresses = new HashMap<>();

  public TelegramBot(DefaultBotOptions options) {
    super(options);
  }

  public TelegramBot() {

  }

  @PostConstruct
  public void postConstruct() {
    messagePostLoaders.forEach(messagePostLoader -> messagePostLoader.postRun(this));
  }

  @Override
  public void onUpdateReceived(Update update) {
    Message message = update.hasChannelPost() ? update.getChannelPost() : update.getMessage();
    if (message != null) {
      TelegramMessageEntity telegramMessageEntity = new TelegramMessageEntity(message, this);
      for (MessageProcessor messageProcessor : messageProcessors) {
        try {
          if (messageProcessor.isNeedToHandle(telegramMessageEntity)) {
            messageProcessor.process(telegramMessageEntity);
            return;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      commonMessageHandler.handleMessage(telegramMessageEntity);
    } else if (update.hasCallbackQuery()) {
      for (MessageProcessor messageProcessor : messageProcessors) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try {
          if (messageProcessor.isRegardingCallback(callbackQuery)) {
            messageProcessor.processCallBack(callbackQuery, this);
            return;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    }
  }

  @Override
  public String getBotUsername() {
    return botName;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  @Override
  public void init() {
    if (StringUtils.isNotEmpty(botName) && StringUtils.isNotEmpty(botToken)) {
      TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
      try {
        telegramBotsApi.registerBot(this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void destroy() {

  }

  @Override
  public void sendMessage(String text, String chatId) {
    try {
      SendMessage sendMessage = new SendMessage();
      sendMessage.setText(text);
      sendMessage.setChatId(chatId);
      execute(sendMessage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
