package ru.holyway.botplatform.telegram;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.AbsSender;
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
            sender.sendMessage(sendMessage);
            if (false) {
                throw new TelegramApiException("");
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public Message getMessage() {
        return message;
    }

    public AbsSender getSender() {
        return sender;
    }
}
