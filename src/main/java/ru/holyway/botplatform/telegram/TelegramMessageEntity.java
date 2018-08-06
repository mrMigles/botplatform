package ru.holyway.botplatform.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.MessageEntity;

/**
 * Created by Sergey on 1/17/2017.
 */
public class TelegramMessageEntity implements MessageEntity {

    private final Message message;

    private final AbsSender sender;

    public TelegramMessageEntity(Message message, AbsSender sender) {
        this.message = message;
        this.sender = sender;
    }

    @Override
    public String getText() {
        return message.getText();
    }

    @Override
    public String getSenderName() {
        return message.getFrom().getFirstName() + " " + message.getFrom().getLastName();
    }

    @Override
    public String getSenderLogin() {
        return message.getFrom().getUserName();
    }

    @Override
    public String getChatId() {
        return String.valueOf(message.getChatId());
    }

    @Override
    public void reply(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(getChatId());
        sendMessage.setText(text);
        try {
            sender.execute(sendMessage);
            if (false) {
                throw new TelegramApiException("");
            }
        } catch (TelegramApiException telegramApiException) {
            telegramApiException.printStackTrace();
        }
    }

    public Message getMessage() {
        return message;
    }

    public AbsSender getSender() {
        return sender;
    }
}
