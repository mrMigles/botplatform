package ru.holyway.botplatform.telegram;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.core.CommonHandler;
import ru.holyway.botplatform.telegram.processor.MessagePostLoader;
import ru.holyway.botplatform.telegram.processor.MessageProcessor;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

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

  private final List<BlockingQueue<Update>> queues;
  private final List<Thread> consumers;
  private final Integer threadCount;

  private BlockingQueue<InlineQuery> inlineQueryBlockingQueue;
  private Thread inlineQueryWorker;

  private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBot.class);

//  public TelegramBot(DefaultBotOptions options) {
//    super(options);
//  }

  public TelegramBot(final Integer threadCount, final Integer queueSize) {
    queues = new ArrayList<>(threadCount);
    consumers = new ArrayList<>(threadCount);

    for (int i = 0; i < threadCount; i++) {
      BlockingQueue<Update> queue = new LinkedBlockingDeque<>(queueSize);
      queues.add(queue);
      Thread consumer = new Thread(new WorkerThread(queue, this));
      consumers.add(consumer);
      consumer.start();
    }
    this.threadCount = threadCount;

    inlineQueryBlockingQueue = new LinkedBlockingDeque<>(queueSize);
    inlineQueryWorker = new Thread(new InlineWorker(this, inlineQueryBlockingQueue));
    inlineQueryWorker.start();
  }

  @PostConstruct
  public void postConstruct() {
    messagePostLoaders.forEach(messagePostLoader -> messagePostLoader.postRun(this));
  }

  @Override
  public void onUpdateReceived(Update update) {
    Message message = update.hasChannelPost() ? update.getChannelPost() : update.getMessage();
    if (message == null) {
      if (update.hasCallbackQuery()) {
        message = (Message) update.getCallbackQuery().getMessage();
      } else if (update.hasInlineQuery()) {
        try {
          inlineQueryBlockingQueue.put(update.getInlineQuery());
        } catch (InterruptedException e) {
          LOGGER.error("InterruptedException due to adding inline worker", e);
        }
        return;
      } else {
        return;
      }
    }
    int partition = partition(message.getChatId().toString(), threadCount);
    try {
      if (queues.get(partition).remainingCapacity() < 10) {
        LOGGER.error("Critical remaining capacity: " + message.getChatId());
      }
      queues.get(partition).put(update);
    } catch (InterruptedException e) {
      LOGGER.error("Interrupted while queuing update: ", e);
      Thread.currentThread().interrupt();
    } catch (Throwable e) {
      LOGGER.error("Error occurred during execution: ", e);
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
      try {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(this);
      } catch (Exception e) {
        LOGGER.error("Error occurred during execution: ", e);
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
      LOGGER.error("Error occurred during execution: ", e);
    }
  }

  public int partition(String key, int numPartitions) {
    return Math.abs(key.hashCode() % numPartitions);
  }

  private class WorkerThread implements Runnable {

    private final BlockingQueue<Update> queue;
    private final AbsSender sender;

    private WorkerThread(BlockingQueue<Update> queue, AbsSender sender) {
      this.queue = queue;
      this.sender = sender;
    }

    @Override
    public void run() {
      while (true) {
        try {
          // Dequeue a chat ID from the priority queue
          Update update = queue.take();
          onUpdateReceivedInternal(update);
        } catch (InterruptedException e) {
          LOGGER.error("Interrupt Error occurred during execution main: ", e);
        } catch (Exception e) {
          LOGGER.error("Error occurred during execution main: ", e);
        }
      }
    }

    private void onUpdateReceivedInternal(Update update) {
      Message message = update.hasChannelPost() ? update.getChannelPost() : update.getMessage();

      if (message != null) {
        TelegramMessageEntity telegramMessageEntity = new TelegramMessageEntity(message, update.getCallbackQuery(), sender);
        for (MessageProcessor messageProcessor : messageProcessors) {
          try {
            if (messageProcessor.isNeedToHandle(telegramMessageEntity)) {
              messageProcessor.process(telegramMessageEntity);
              break;
            }
          } catch (Throwable e) {
            LOGGER.error("Error occurred during execution: ", e);
          }
        }
        try {
          commonMessageHandler.handleMessage(telegramMessageEntity);
        } catch (Throwable e) {
          LOGGER.error("Error occurred during execution: ", e);
        }
      } else if (update.hasCallbackQuery()) {
        for (MessageProcessor messageProcessor : messageProcessors) {
          CallbackQuery callbackQuery = update.getCallbackQuery();
          try {
            if (messageProcessor.isRegardingCallback(callbackQuery)) {
              messageProcessor.processCallBack(callbackQuery, sender);
              return;
            }
          } catch (Throwable e) {
            LOGGER.error("Error occurred during execution: ", e);
          }
        }
      }
    }
  }
}
