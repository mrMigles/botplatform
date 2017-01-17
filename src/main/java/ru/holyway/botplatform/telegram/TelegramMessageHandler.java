package ru.holyway.botplatform.telegram;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.CommonMessageHandler;
import ru.holyway.botplatform.core.MessageEntity;

/**
 * Created by Sergey on 1/17/2017.
 */
public class TelegramMessageHandler extends CommonMessageHandler {
    @Override
    protected void sendMessageInternal(MessageEntity messageEntity, String text) {
        messageEntity.reply(text);
    }
}
