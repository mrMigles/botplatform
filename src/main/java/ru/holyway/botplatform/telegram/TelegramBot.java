package ru.holyway.botplatform.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.core.MessageHandler;

/**
 * Created by Sergey on 1/17/2017.
 */
@Component
public class TelegramBot extends TelegramLongPollingBot implements Bot {

    private static final String botName = "pakhom_bot";
    private static final String botToken = "261215240:AAEi6m-VCtP_wUxNaoaMw-Ffc4Ls6btB6Nk";

    @Autowired
    @Qualifier("telegramMessageHandler")
    private MessageHandler messageHandler;

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null) {
            messageHandler.handleMessage(new TelegramMessageEntity(message, this));
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
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {

    }
}
