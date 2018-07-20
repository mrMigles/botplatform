package ru.holyway.botplatform.telegram.processor;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.Tesseract1;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(4)
public class ReadTextOnPhotoProcessor implements MessageProcessor {

  private Map<String, Boolean> askImage = new ConcurrentHashMap<>();

  private final String token;

  private Tesseract1 instance = new Tesseract1();

  public ReadTextOnPhotoProcessor(@Value("${credential.telegram.token}") String token) {
    this.token = token;
    instance.setLanguage("rus");
  }

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    return StringUtils.containsIgnoreCase(messageEntity.getText(), "/read") || StringUtils
        .containsIgnoreCase(messageEntity.getText(), "/cancel") || (askImage
        .get(messageEntity.getSenderLogin()) != null
        && messageEntity.getMessage().getPhoto() != null);
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    if (StringUtils.containsIgnoreCase(messageEntity.getText(), "/cancel")) {
      messageEntity.getSender().execute(
          new SendMessage().setText(messageEntity.getSenderName() + ", хорошо, забыли.")
              .setChatId(messageEntity.getChatId()));
      askImage.remove(messageEntity.getSenderLogin());

    } else if (askImage.get(messageEntity.getSenderLogin()) != null
        && messageEntity.getMessage().getPhoto() != null) {
      final String url = messageEntity.getSender().execute(new GetFile().setFileId(
          messageEntity.getMessage().getPhoto()
              .get(messageEntity.getMessage().getPhoto().size() - 1).getFileId()))
          .getFileUrl(token);
      String text = "";
      try {
        BufferedImage bufferedImage = ImageIO.read(new URL(url));
        text = instance.doOCR(bufferedImage);
      } catch (Throwable e) {
        System.out.println("Err: " + e);
        e.printStackTrace();
      }
      askImage.remove(messageEntity.getSenderLogin());
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText(text));
    } else {
      askImage.put(messageEntity.getSenderLogin(), true);
      messageEntity.getSender()
          .execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Пришлите фото"));
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
