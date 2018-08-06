package ru.holyway.botplatform.telegram.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.CommonHandler;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

@Component
@Order(100)
public class CommonMessageProcessor implements MessageProcessor {

    @Autowired
    private CommonHandler commonMessageHandler;

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        return true;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        commonMessageHandler.handleMessage(messageEntity);
    }

    @Override
    public boolean isRegardingCallback(CallbackQuery callbackQuery) {
        return false;
    }

    @Override
    public void processCallBack(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

    }
}
