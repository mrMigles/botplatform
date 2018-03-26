package ru.holyway.botplatform.telegram.processor;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;
import ru.holyway.botplatform.web.entities.ImageResponse;
import ru.holyway.botplatform.web.entities.ImageResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(10)
public class GeneratorPhrasesProcessor implements MessageProcessor {

    private Map<String, String> wordsToChat = new ConcurrentHashMap<>();

    private Map<String, BufferedImage> inageToChat = new ConcurrentHashMap<>();

    private Map<String, Boolean> askWord = new ConcurrentHashMap<>();

    private Map<String, Boolean> askImage = new ConcurrentHashMap<>();

    private final String token;

    public GeneratorPhrasesProcessor(@Value("${credential.telegram.token}") String token) {
        this.token = token;
    }

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        final String mes = messageEntity.getText();

        return messageEntity.getMessage().hasPhoto() || StringUtils.isNotEmpty(mes) && (mes.contains("/gen") || mes.contains("/rep") || mes.contains("/skip") || mes.contains("/cancel")) || askWord.get(messageEntity.getSenderLogin()) != null;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        final String mes = messageEntity.getText();
        if (StringUtils.isNotEmpty(mes) && mes.contains("/cancel")) {
            if (askImage.get(messageEntity.getSenderLogin()) != null || askWord.get(messageEntity.getSenderLogin()) != null) {
                askImage.remove(messageEntity.getSenderLogin());
                askWord.remove(messageEntity.getSenderLogin());
                messageEntity.getSender().execute(new SendMessage().setText(messageEntity.getSenderName() + ", хорошо, забыли.").setChatId(messageEntity.getChatId()));
                return;
            }
        }
        if (StringUtils.isNotEmpty(mes) && mes.contains("/rep")) {
            if (wordsToChat.get(messageEntity.getChatId()) != null && inageToChat.get(messageEntity.getChatId()) != null) {
                final String message = generate(wordsToChat.get(messageEntity.getChatId()));
                sendMeme(messageEntity.getChatId(), message, messageEntity);
            } else {
                messageEntity.getSender().execute(new SendMessage().setText("Давай нормально, а?").setChatId(messageEntity.getChatId()));
            }
            return;
        }
        if (StringUtils.isNotEmpty(mes) && mes.contains("/gen")) {
            messageEntity.getSender().execute(new SendMessage().setText("Пришлите фото (или /skip если доверяете мне)").setChatId(messageEntity.getChatId()));
            askImage.put(messageEntity.getSenderLogin(), true);
            return;
        }
        if (askImage.get(messageEntity.getSenderLogin()) != null && messageEntity.getMessage().hasPhoto()) {
            final String url = messageEntity.getSender().execute(new GetFile().setFileId(messageEntity.getMessage().getPhoto().get(messageEntity.getMessage().getPhoto().size() - 1).getFileId())).getFileUrl(token);
            try {
                BufferedImage bufferedImage = ImageIO.read(new URL(url));
                inageToChat.put(messageEntity.getChatId(), bufferedImage);
                messageEntity.getSender().execute(new SendMessage().setText("Напишите фразу:").setChatId(messageEntity.getChatId()));
                askWord.put(messageEntity.getSenderLogin(), true);
                askImage.remove(messageEntity.getSenderLogin());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotEmpty(mes) && mes.contains("/skip") && askImage.get(messageEntity.getSenderLogin()) != null) {
            messageEntity.getSender().execute(new SendMessage().setText("Напишите фразу:").setChatId(messageEntity.getChatId()));
            askWord.put(messageEntity.getSenderLogin(), true);
            return;
        }
        if (StringUtils.isNotEmpty(mes) && askWord.get(messageEntity.getSenderLogin()) != null) {
            final String message = generate(mes);
            wordsToChat.put(messageEntity.getChatId(), mes);
            askWord.remove(messageEntity.getSenderLogin());
            if (askImage.get(messageEntity.getSenderLogin()) != null) {
                try {
                    SearchResults searchResults = BingImageSearch.SearchImages(mes);
                    final String json = searchResults.jsonResponse;
                    ImageResponse imageResponse = new Gson().fromJson(json, ImageResponse.class);
                    for (ImageResult imageResult : imageResponse.value) {
                        final String url = imageResult.contentUrl;
                        System.out.println("Image " + url);
                        BufferedImage bufferedImage = ImageIO.read(new URL(url));
                        if (bufferedImage != null) {
                            inageToChat.put(messageEntity.getChatId(), bufferedImage);
                            askImage.remove(messageEntity.getSenderLogin());
                            sendMeme(messageEntity.getChatId(), message, messageEntity);
                            return;
                        }
                    }


                } catch (Exception e) {
                    System.out.println("Errror: " + e);
                }
                messageEntity.getSender().execute(new SendMessage().setText("Упс").setChatId(messageEntity.getChatId()));
            }

        }
    }

    @Override
    public boolean isRegardingCallback(CallbackQuery callbackQuery) {
        return false;
    }

    @Override
    public void processCallBack(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

    }

    private String generate(final String word) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> hashMap = new LinkedMultiValueMap<String, String>();
        hashMap.add("moduleName", "TitleGen");
        hashMap.add("cmd", "gen");
        hashMap.add("word", word);
        hashMap.add("language", "ru");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(hashMap, headers);

        final String input = new RestTemplate().postForObject(URI.create("http://title.web-canape.ru/ajax/ajax.php"), request, String.class);
        String search = "<div class=\\\"js-full_text\\\" style=\\\"display: none;\\\">";
        String searchEnd = "<\\/div>";
        int start = input.indexOf(search) + search.length();
        int end = input.indexOf(searchEnd, start);
        System.out.println(StringEscapeUtils.unescapeJava(input.substring(start, end)));
        return StringEscapeUtils.unescapeJava(input.substring(start, end));
    }

    private void sendMeme(final String chatID, final String text, TelegramMessageEntity telegramMessageEntity) throws TelegramApiException {
        try {
            BufferedImage result = ImageOverlay.overlay(inageToChat.get(chatID), "", text);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(result, "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            telegramMessageEntity.getSender().sendPhoto(new SendPhoto().setNewPhoto("new", is).setChatId(telegramMessageEntity.getChatId()).setCaption(text));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
