package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
public class AdminMessageProcessor implements MessageProcessor {
    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        String mes = messageEntity.getText();
        if (StringUtils.equalsIgnoreCase(mes, "сука") || StringUtils.equalsIgnoreCase(mes, "бля")) {
            return true;
        }
        return false;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        messageEntity.getSender().execute(new DeleteMessage().setChatId(messageEntity.getChatId()).setMessageId(messageEntity.getMessage().getMessageId()));
    }

    @Override
    public boolean isRegardingCallback(CallbackQuery callbackQuery) {
        return false;
    }

    @Override
    public void processCallBack(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

    }
}
