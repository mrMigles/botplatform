package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(10)
public class GeneratorPhrasesProcessor implements MessageProcessor {

    private Map<String, String> wordsToChat = new ConcurrentHashMap<>();

    private Map<String, Boolean> askWord = new ConcurrentHashMap<>();

    @Override

    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        if (StringUtils.isNotEmpty(mes) && (mes.equalsIgnoreCase("/gen") || mes.equalsIgnoreCase("/rep"))) {
            return true;
        } else return askWord.get(messageEntity.getSenderLogin()) != null;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        final String mes = messageEntity.getText();
        if (mes.equalsIgnoreCase("/rep")) {
            if (wordsToChat.get(messageEntity.getChatId()) != null) {
                final String message = generate(wordsToChat.get(messageEntity.getChatId()));
                messageEntity.getSender().execute(new SendMessage().setText(message).setChatId(messageEntity.getChatId()));
            } else {
                messageEntity.getSender().execute(new SendMessage().setText("Сначала напишите слово").setChatId(messageEntity.getChatId()));
                askWord.put(messageEntity.getSenderLogin(), true);
            }
            return;
        }
        if (mes.equalsIgnoreCase("/gen")) {
            messageEntity.getSender().execute(new SendMessage().setText("Напишите слово:").setChatId(messageEntity.getChatId()));
            askWord.put(messageEntity.getSenderLogin(), true);
            return;
        }
        if (askWord.get(messageEntity.getSenderLogin()) != null) {
            final String message = generate(mes);
            wordsToChat.put(messageEntity.getChatId(), mes);
            messageEntity.getSender().execute(new SendMessage().setText(message).setChatId(messageEntity.getChatId()));
            askWord.remove(messageEntity.getSenderLogin());
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
}
