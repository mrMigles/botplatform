package ru.holyway.botplatform.telegram;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.core.entity.AdressResponse;
import ru.holyway.botplatform.telegram.processor.MessageProcessor;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergey on 1/17/2017.
 */
@Component
@Order(1)
public class TelegramBot extends TelegramLongPollingBot implements Bot {

    @Value("${credential.telegram.login}")
    private String botName;

    @Value("${credential.telegram.token}")
    private String botToken;

    @Autowired
    private List<MessageProcessor> messageProcessors;

    private Map<Integer, String> locations = new HashMap<>();

    private Map<Integer, String> realAddresses = new HashMap<>();


    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasEditedMessage()) {
            Message editedMessage = update.getEditedMessage();
            if (editedMessage.getLocation() != null) {
                final String locationValue = editedMessage.getLocation().toString();
                final String previous = locations.get(editedMessage.getFrom().getId());
                if (previous == null) {
                    locations.put(editedMessage.getFrom().getId(), locationValue);
                    try {
                        final String locRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + editedMessage.getLocation().getLatitude() + "," + editedMessage.getLocation().getLongitude() + "&key=AIzaSyAP9HBBVRLvlEF6BBAS_2tJbx7KTBKGHQI&language=ru";
                        ResponseEntity<AdressResponse> adressResponse = new RestTemplate().getForEntity(URI.create(locRequest), AdressResponse.class);
                        AdressResponse response = adressResponse.getBody();
                        final String realAddress = response.results.get(0).formatted_address;
                        execute(new SendMessage().setText("Address of user " + editedMessage.getFrom().getFirstName() + " is \n" + realAddress).setChatId(editedMessage.getChatId()));
                        realAddresses.put(editedMessage.getFrom().getId(), realAddress);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!locationValue.equalsIgnoreCase(previous)) {
                        locations.put(editedMessage.getFrom().getId(), locationValue);
                        try {
                            final String locRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + editedMessage.getLocation().getLatitude() + "," + editedMessage.getLocation().getLongitude() + "&key=AIzaSyAP9HBBVRLvlEF6BBAS_2tJbx7KTBKGHQI&language=ru";
                            ResponseEntity<AdressResponse> adressResponse = new RestTemplate().getForEntity(URI.create(locRequest), AdressResponse.class);
                            AdressResponse response = adressResponse.getBody();
                            final String realAddress = response.results.get(0).formatted_address;
                            if (realAddresses.get(editedMessage.getFrom().getId()) != null) {
                                if (!realAddress.equalsIgnoreCase(realAddresses.get(editedMessage.getFrom().getId()))) {
                                    execute(new SendMessage().setText("Address of user " + editedMessage.getFrom().getFirstName() + " has ben changed to \n" + realAddress).setChatId(editedMessage.getChatId()));
                                    realAddresses.put(editedMessage.getFrom().getId(), realAddress);
                                }
                            }

                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
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

    /**
     * @param text          The text that should be shown
     * @param alert         If the text should be shown as a alert or not
     * @param callbackquery
     */
    private void sendAnswerCallbackQuery(final String text, boolean alert, CallbackQuery callbackquery) throws Exception {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        answerCallbackQuery(answerCallbackQuery);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(String.valueOf(callbackquery.getMessage().getChatId()));
        editMessageReplyMarkup.setMessageId(callbackquery.getMessage().getMessageId());

        editMessageReplyMarkup(editMessageReplyMarkup);
    }

}
