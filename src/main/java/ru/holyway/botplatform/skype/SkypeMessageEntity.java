package ru.holyway.botplatform.skype;

import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import ru.holyway.botplatform.core.MessageEntity;

/**
 * Created by Sergey on 1/17/2017.
 */
public class SkypeMessageEntity implements MessageEntity {

    private final MessageReceivedEvent messageReceivedEvent;

    public SkypeMessageEntity(MessageReceivedEvent messageReceivedEvent) {
        this.messageReceivedEvent = messageReceivedEvent;
    }

    @Override
    public String getText() {
        return messageReceivedEvent.getMessage().getContent().asPlaintext();
    }

    @Override
    public String getSender() {
        try {
            return messageReceivedEvent.getMessage().getSender().getDisplayName();
        } catch (ConnectionException e) {
            e.printStackTrace();
            return "Пушкин";
        }
    }

    @Override
    public String getChatId() {
        return messageReceivedEvent.getMessage().getChat().getIdentity();
    }

    @Override
    public void reply(String text) {
        try {
            messageReceivedEvent.getChat().sendMessage(text);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }
}
