package ru.holyway.botplatform.telegram.processor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(50)
public class RandomMemeProcessor implements MessageProcessor {

  private Map<Long, Message> lastMessageMap = new ConcurrentHashMap<>();
  private BufferedImage dynoTemplate;
  private BufferedImage catTemplate;

  @PostConstruct
  public void postConstruct() throws IOException {
    dynoTemplate = ImageIO
        .read(RandomMemeProcessor.class.getResourceAsStream("/dyno.jpg"));

    catTemplate = ImageIO
        .read(RandomMemeProcessor.class.getResourceAsStream("/cat.jpg"));
  }

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    if (messageEntity.getMessage().hasText()) {
      Message lastMessage = lastMessageMap.get(messageEntity.getMessage().getChatId());
      if (lastMessage != null) {
        if (lastMessage.getFrom().getId().equals(messageEntity.getMessage().getFrom().getId())) {
          if (messageEntity.getMessage().getDate() - lastMessage.getDate() < TimeUnit.SECONDS
              .toMillis(30)) {
            return true;
          }
        }
      }
      lastMessageMap.put(messageEntity.getMessage().getChatId(), messageEntity.getMessage());
    }
    return false;
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    Message lastMessage = lastMessageMap.get(messageEntity.getMessage().getChatId());
    String curText = messageEntity.getText();
    if (isOnlyCapital(curText)) {
      try {
        if (dynoTemplate != null && catTemplate != null) {
          BufferedImage result;
          if (new Random().nextBoolean()) {
            result = CatImageOverlay
                .overlay(catTemplate, lastMessage.getText(), messageEntity.getText());
          } else {
            result = DynoImageOverlay
                .overlay(dynoTemplate, lastMessage.getText(), messageEntity.getText());
          }
          ByteArrayOutputStream os = new ByteArrayOutputStream();
          ImageIO.write(result, "jpg", os);
          InputStream is = new ByteArrayInputStream(os.toByteArray());
          messageEntity.getSender().execute(
              new SendPhoto().setPhoto("new", is).setChatId(messageEntity.getChatId()));
        }
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
      lastMessageMap.remove(messageEntity.getMessage().getChatId());
    } else {
      lastMessageMap.put(messageEntity.getMessage().getChatId(), messageEntity.getMessage());
    }
  }

  private static boolean isOnlyCapital(String str) {
    char ch;
    boolean capitalFlag = false;
    boolean lowerCaseFlag = false;
    for (int i = 0; i < str.length(); i++) {
      ch = str.charAt(i);
      if (Character.isUpperCase(ch)) {
        capitalFlag = true;
      } else if (Character.isLowerCase(ch)) {
        lowerCaseFlag = true;
      }
    }
    return !lowerCaseFlag && capitalFlag;
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
