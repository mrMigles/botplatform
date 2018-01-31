package ru.holyway.botplatform.telegram;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.core.CommonHandler;
import ru.holyway.botplatform.telegram.processor.MessageProcessor;

import java.util.List;

/**
 * Created by Sergey on 1/17/2017.
 */
@Component
public class TelegramBot extends TelegramLongPollingBot implements Bot {

    @Value("${credential.telegram.login}")
    private String botName;

    @Value("${credential.telegram.token}")
    private String botToken;

    @Autowired
    private CommonHandler commonHandler;

    @Autowired
    private List<MessageProcessor> messageProcessors;


    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null) {
            TelegramMessageEntity telegramMessageEntity = new TelegramMessageEntity(message, this);
            for (MessageProcessor messageProcessor : messageProcessors) {
                if (messageProcessor.isNeedToHandle(telegramMessageEntity)) {
                    try {
                        messageProcessor.process(telegramMessageEntity);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            commonHandler.handleMessage(telegramMessageEntity);
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
        //ApiContextInitializer.init();
        if (StringUtils.isNotEmpty(botName) && StringUtils.isNotEmpty(botToken)) {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            try {
                telegramBotsApi.registerBot(this);
            } catch (TelegramApiException e) {
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
            sendMessage(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
