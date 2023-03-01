package ru.holyway.botplatform.core;

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.handler.MessageHandler;

/**
 * Created by Sergey on 1/17/2017.
 */
public class CommonMessageHandler implements CommonHandler {

  @Autowired
  private DataHelper dataHelper;

  @Autowired
  @Qualifier("orderedMessageHandlers")
  private List<MessageHandler> messageHandlers;

  @Autowired
  private Context context;

  @Value("${bot.config.silentPeriod}")
  private String silentPeriodString;

  private long srartTime = 0;
  private long silentPeriod = TimeUnit.SECONDS.toMillis(60);


  public CommonMessageHandler() {

  }

  @PostConstruct
  public void postConstruct() {
    srartTime = System.currentTimeMillis();
    if (StringUtils.isNotEmpty(silentPeriodString)) {
      silentPeriod = TimeUnit.SECONDS.toMillis(Long.parseLong(silentPeriodString));
    }
  }

  @Override
  public String generateAnswer(MessageEntity messageEntity) {
    if (messageEntity != null) {

      for (MessageHandler messageHandler : messageHandlers) {
        try {
          String message = messageHandler.provideAnswer(messageEntity);
          if (message != null) {
            return message;
          }
        } catch (ProcessStopException e) {
          System.out.println("Stop because: " + e.getMessage());
          break;
        }
      }
    }
    return null;
  }

  @Override
  public void handleMessage(MessageEntity messageEntity) {
    final String answer = generateAnswer(messageEntity);
    try {
      if (!StringUtils.isEmpty(answer)) {
        sendMessage(messageEntity, answer);
      }
    } catch (Exception e){
      e.printStackTrace();
      System.out.println(e);
    }

  }

  private void sendMessage(MessageEntity messageEntity, String text) {
    long currentTime = System.currentTimeMillis();
    if (currentTime - this.srartTime > silentPeriod) {
      sendMessageInternal(messageEntity, text);
      context.setLastStamp(System.currentTimeMillis());
      context.incrementCount();
    }
  }

  protected void sendMessageInternal(MessageEntity messageEntity, String text) {
    messageEntity.reply(text);
  }
}
