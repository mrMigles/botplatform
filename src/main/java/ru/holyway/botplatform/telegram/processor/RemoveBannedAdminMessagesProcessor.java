package ru.holyway.botplatform.telegram.processor;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.ProcessorsContext;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.Set;

@Component
@Order(1)
public class RemoveBannedAdminMessagesProcessor implements MessageProcessor {

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        Set<Integer> bannedForChat = ProcessorsContext.getInstance().getBannedAdmins(messageEntity.getChatId());
        return bannedForChat != null && bannedForChat.contains(messageEntity.getMessage().getFrom().getId());
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        messageEntity.getSender().execute(new DeleteMessage().setMessageId(messageEntity.getMessage().getMessageId()).setChatId(messageEntity.getChatId()));
    }

    @Override
    public boolean isRegardingCallback(CallbackQuery callbackQuery) {
        return false;
    }

    @Override
    public void processCallBack(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

    }
}
