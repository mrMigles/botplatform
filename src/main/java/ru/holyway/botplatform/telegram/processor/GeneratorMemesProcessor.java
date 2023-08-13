package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(101)
public class GeneratorMemesProcessor implements MessageProcessor {

  private Map<String, String> wordsToChat = new ConcurrentHashMap<>();

  private Map<String, BufferedImage> inageToChat = new ConcurrentHashMap<>();

  private Map<String, Boolean> askWord = new ConcurrentHashMap<>();

  private Map<String, Boolean> askImage = new ConcurrentHashMap<>();

  private final String token;

  public GeneratorMemesProcessor(@Value("${credential.telegram.token}") String token) {
    this.token = token;
  }

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    final String mes = messageEntity.getText();

    return messageEntity.getMessage().hasPhoto() || StringUtils.isNotEmpty(mes) && (
        mes.contains("/meme") || StringUtils.containsIgnoreCase(mes, "сделай мем") || mes
            .contains("/cancel")) || askWord.get(messageEntity.getSenderLogin()) != null;
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    final String mes = messageEntity.getText();
    if (StringUtils.isNotEmpty(mes) && mes.contains("/cancel")) {
      if (askImage.get(messageEntity.getSenderLogin()) != null
          || askWord.get(messageEntity.getSenderLogin()) != null) {
        askImage.remove(messageEntity.getSenderLogin());
        askWord.remove(messageEntity.getSenderLogin());
        messageEntity.getSender().execute(
            SendMessage.builder().text(messageEntity.getSenderName() + ", хорошо, забыли.")
                .chatId(messageEntity.getChatId()).build());
        return;
      }
    }
    if (StringUtils.isNotEmpty(mes) && (mes.contains("/meme") || StringUtils
        .containsIgnoreCase(mes, "сделай мем"))) {
      messageEntity.getSender().execute(
          SendMessage.builder().text("Пришлите фото для мема").chatId(messageEntity.getChatId()).build());
      askImage.put(messageEntity.getSenderLogin(), true);
      return;
    }
    if (askImage.get(messageEntity.getSenderLogin()) != null && messageEntity.getMessage()
        .hasPhoto()) {
      final String url = messageEntity.getSender().execute(GetFile.builder().fileId(
              messageEntity.getMessage().getPhoto()
                  .get(messageEntity.getMessage().getPhoto().size() - 1).getFileId()).build())
          .getFileUrl(token);
      try {
        BufferedImage bufferedImage = ImageIO.read(new URL(url));
        inageToChat.put(messageEntity.getChatId(), bufferedImage);
        messageEntity.getSender().execute(
            SendMessage.builder().text("Напишите фразу для мема")
                .chatId(messageEntity.getChatId()).build());
        askWord.put(messageEntity.getSenderLogin(), true);
        askImage.remove(messageEntity.getSenderLogin());
        return;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (StringUtils.isNotEmpty(mes) && askWord.get(messageEntity.getSenderLogin()) != null) {
      wordsToChat.put(messageEntity.getChatId(), mes);
      askWord.remove(messageEntity.getSenderLogin());
      sendMeme(messageEntity.getChatId(), mes, messageEntity);
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

  private void sendMeme(final String chatID, final String text,
                        TelegramMessageEntity telegramMessageEntity) throws TelegramApiException {
    try {
      BufferedImage result = MemeImageOverlay.overlay(inageToChat.get(chatID), "", text);
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(result, "jpg", os);
      InputStream is = new ByteArrayInputStream(os.toByteArray());
      telegramMessageEntity.getSender().execute(
          SendPhoto.builder().photo(new InputFile(is, "new")).chatId(telegramMessageEntity.getChatId())
              .caption(text).build());
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
