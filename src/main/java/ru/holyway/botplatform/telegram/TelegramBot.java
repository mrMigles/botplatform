package ru.holyway.botplatform.telegram;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.telegram.processor.MessageProcessor;

import java.util.List;

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


    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
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
