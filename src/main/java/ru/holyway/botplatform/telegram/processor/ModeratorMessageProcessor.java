package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


public class ModeratorMessageProcessor implements MessageProcessor {

    private List<String> matWords = new ArrayList<>();

    @PostConstruct
    private void postConstruct() {
        matWords.add("сука");
        matWords.add("блядь");
        matWords.add("бля");
        matWords.add("сука");
        matWords.add("сук");
        matWords.add("пиздец");
        matWords.add("пиздос");
        matWords.add("ебанутый");
        matWords.add("ебанутая");
        matWords.add("долбаеб");
        matWords.add("ебать");
        matWords.add("еба");
        matWords.add("хуй");
        matWords.add("хуев");
        matWords.add("алгоритм");
        matWords.add("курс");
        matWords.add("машин лернинг");
        matWords.add("ML");
        matWords.add("градиентный спуск");
        matWords.add("скрутк");
        matWords.add("лернинг");
        matWords.add("свертк");
        matWords.add("машинное обучен");
        matWords.add("ассоциативный");
    }

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        String mes = messageEntity.getText();
        if (StringUtils.isNotEmpty(mes)) {
            for (String mat : matWords) {
                if (StringUtils.containsIgnoreCase(mes, mat)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        String originalMessage = messageEntity.getText();
        messageEntity.getSender().execute(new DeleteMessage().setChatId(messageEntity.getChatId()).setMessageId(messageEntity.getMessage().getMessageId()));
        if (originalMessage.length() > 10) {
            for (String mat : matWords) {
                originalMessage = originalMessage.replaceAll(mat, "****");
            }
            messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId()).setText(messageEntity.getMessage().getFrom().getFirstName()
                    + " хотел ссказать: \n" + originalMessage));
        }
    }

    @Override
    public boolean isRegardingCallback(CallbackQuery callbackQuery) {
        return false;
    }

    @Override
    public void processCallBack(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

    }
}
