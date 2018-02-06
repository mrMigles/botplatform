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

import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class AnonymMessageProcessor implements MessageProcessor {

    private List<String> anonymChat = new ArrayList<>();

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        if (StringUtils.isNotEmpty(mes)) {
            if (mes.contains("/anon")) {
                return true;
            }
        }
        return anonymChat.contains(messageEntity.getChatId());
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        final String mes = messageEntity.getText();
        if (StringUtils.isNotEmpty(mes)) {
            if (mes.contains("/anon")) {
                if (anonymChat.contains(messageEntity.getChatId())) {
                    anonymChat.remove(messageEntity.getChatId());
                    messageEntity.getSender().execute(new SendMessage().setText("Ok").setChatId(messageEntity.getChatId()));
                } else {
                    anonymChat.add(messageEntity.getChatId());
                    messageEntity.getSender().execute(new SendMessage().setText("Ok").setChatId(messageEntity.getChatId()));
                }
            } else {
                messageEntity.getSender().execute(new DeleteMessage().setChatId(messageEntity.getChatId()).setMessageId(messageEntity.getMessage().getMessageId()));
                messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Кто-то сказал: \n" + mes));
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
}
