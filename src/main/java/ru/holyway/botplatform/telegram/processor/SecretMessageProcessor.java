package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SecretMessageProcessor implements MessageProcessor {

    private Map<String, List<Integer>> secretMessages = new HashMap<>();

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        if (mes.contains("/secret") || secretMessages.get(messageEntity.getChatId()) != null) {
            return true;
        }
        return false;
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        List<Integer> messages = secretMessages.get(messageEntity.getChatId());
        if (messages != null) {
            if (StringUtils.isNotEmpty(messageEntity.getText()) && StringUtils.containsIgnoreCase(messageEntity.getText(), "/secret")) {
                messages.add(messageEntity.getMessage().getMessageId());
                for (Integer integer : messages) {
                    try {
                        messageEntity.getSender().execute(new DeleteMessage().setChatId(messageEntity.getChatId()).setMessageId(integer));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    secretMessages.remove(messageEntity.getChatId());
                }
            } else {
                messages.add(messageEntity.getMessage().getMessageId());
            }
        } else {
            if (StringUtils.isNotEmpty(messageEntity.getText()) && StringUtils.containsIgnoreCase(messageEntity.getText(), "/secret")) {
                messages = new ArrayList<>();
                messages.add(messageEntity.getMessage().getMessageId());
                Message message = messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("Ах вы шалушники, так и придётся скрыть ваши разговоры."));
                messages.add(message.getMessageId());
                secretMessages.put(messageEntity.getChatId(), messages);
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
