package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.List;

@Component
@Order(1)
public class RemoveLastMessageProcessor implements MessageProcessor {
    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        if (StringUtils.isNotEmpty(mes) && StringUtils.containsIgnoreCase(mes, "Пахом, удали")) {
            if (messageEntity.getMessage().getReplyToMessage() != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        try {
            final Message replyMessage = messageEntity.getMessage().getReplyToMessage();
            if (hasGrants(messageEntity)) {
                for (int i = replyMessage.getMessageId(); i <= messageEntity.getMessage().getMessageId(); i++) {
                    try {
                        messageEntity.getSender().execute(new DeleteMessage().setChatId(messageEntity.getChatId()).setMessageId(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Нимагу"));
            e.printStackTrace();
        }
    }

    private boolean hasGrants(TelegramMessageEntity messageEntity) throws TelegramApiException {
        List<ChatMember> chatMembers = messageEntity.getSender().execute(new GetChatAdministrators().setChatId(messageEntity.getChatId()));
        for (ChatMember chatMember : chatMembers) {
            if (chatMember.getUser().getId().equals(messageEntity.getMessage().getFrom().getId())) {
                if (chatMember.getCanDeleteMessages()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isRegardingCallback(CallbackQuery callbackQuery) {
        return false;
    }

    @Override
    public void processCallBack(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

    }
}
