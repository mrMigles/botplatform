package ru.holyway.botplatform.web;

import ru.holyway.botplatform.core.MessageEntity;

/**
 * Created by Sergey on 10/5/2017.
 */
public class WebMessageEntity implements MessageEntity {

    private final String chatId;
    private final String sender;
    private final String text;

    public WebMessageEntity(String chatId, String sender, String text) {
        this.chatId = chatId;
        this.sender = sender;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getChatId() {
        return chatId;
    }

    @Override
    public void reply(String text) {
        throw new UnsupportedOperationException("Not supported by web");
    }
}
