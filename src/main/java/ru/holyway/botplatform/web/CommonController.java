package ru.holyway.botplatform.web;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.holyway.botplatform.core.CommonMessageHandler;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.MessageHandler;
import ru.holyway.botplatform.skype.SkypeBotSecond;
import ru.holyway.botplatform.telegram.TelegramBot;
import ru.holyway.botplatform.web.entities.SimpleRequest;
import ru.holyway.botplatform.web.entities.SimpleResponse;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seiv0814 on 10-04-17.
 */
@RestController
public class CommonController {

    @Autowired
    TelegramBot bot;

    @Autowired
    MessageHandler messageHandler;

    private String query = "https://ru.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&exintro=&titles=";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity echo() {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/command", method = RequestMethod.POST)
    public ResponseEntity<SimpleResponse> test(@RequestBody SimpleRequest simpleRequest) throws UnsupportedEncodingException {

        final String action = simpleRequest.getResult().getAction();
        if ("whatis".equals(action)) {
            final String findText = simpleRequest.getResult().getParameters().get("findText");
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> stringStringMap = new HashMap<>();
            final String newQuery = query + findText;
            String result = restTemplate.getForObject(newQuery, String.class, stringStringMap);
            result = StringEscapeUtils.unescapeJava(result);
            int start = result.indexOf("extract");
            if (start > 0) {
                int end = result.indexOf("</p>", start);
                if (end > start + 10) {
                    result = result.substring(start + 10, end);
                    result = StringEscapeUtils.escapeHtml4(result);
                    result = result.replaceAll("&[^\\s]*;", "");
                    if (result.length() > 0) {
                        result = "Это " + result;
                        return new ResponseEntity<>(new SimpleResponse(result, result), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(new SimpleResponse("Я многое понимаю, но этого я не понимаю...", "Я многое понимаю, но этого я не понимаю..."), HttpStatus.OK);
        }
        System.out.println("New request: " + simpleRequest.getResult().getAction());
        try {
            return new ResponseEntity<>(new SimpleResponse("Ответ пришёл к тебе для " + simpleRequest.getResult().getParameters(), "Ответ пришёл к тебе для "), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new SimpleResponse("Плохо, очень плохо!", "Плохо, очень плохо!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/server", method = RequestMethod.GET)
    public ResponseEntity<String> restart(@RequestParam("id") String chatId, @RequestParam("message") String message) throws UnsupportedEncodingException {
        bot.sendMessage(message, chatId);
        return ResponseEntity.ok("Ok - " + chatId);
    }

    @RequestMapping(value = "/mes", method = RequestMethod.GET)
    public ResponseEntity<String> message(@RequestParam("id") String chatId, @RequestParam("message") String message) throws UnsupportedEncodingException {

        MessageEntity messageEntity = new WebMessageEntity(chatId, "web", message);
        final String answer = messageHandler.generateAnswer(messageEntity);
        return ResponseEntity.ok(answer);
    }
}
