package ru.holyway.botplatform.telegram.processor;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

@Component
@Order(50)
public class RandomMemeProcessor implements MessageProcessor {

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
    if (messageEntity.getMessage().hasText() && messageEntity.getMessage().isReply() && messageEntity.getMessage().getReplyToMessage().hasText()) {
      if (isOnlyCapital(messageEntity.getText()) && messageEntity.getText().length() >= 3
          && messageEntity.getMessage().getReplyToMessage().getText().length() >= 3) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    Message lastMessage = messageEntity.getMessage().getReplyToMessage();
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
              SendPhoto.builder().photo(new InputFile(is, "new")).chatId(messageEntity.getChatId()).build());
        }
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
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
