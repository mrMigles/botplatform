package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Order(10)
@Component
public class BaDumTssMessageProcessor implements MessageProcessor {

    private Map<String, Integer> last = new ConcurrentHashMap<>();

    @Override
    public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
        return StringUtils.isNotEmpty(messageEntity.getMessage().getText());
    }

    @Override
    public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
        final String mes = messageEntity.getText();
        if (isUpper(mes)) {
            if (last.get(messageEntity.getChatId()) != null && messageEntity.getMessage().getFrom().getId().equals(last.get(messageEntity.getChatId()))) {
                messageEntity.getSender().execute(new SendMessage().setChatId(messageEntity.getChatId()).setText("https://cs8.pikabu.ru/images/previews_comm/2017-09_1/1504277920155595784.png"));
                last.remove(messageEntity.getChatId());
            }
        } else {
            last.put(messageEntity.getChatId(), messageEntity.getMessage().getFrom().getId());
        }
    }

    @Override
    public boolean isRegardingCallback(CallbackQuery callbackQuery) {
        return false;
    }

    @Override
    public void processCallBack(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {

    }

    private boolean isUpper(final String mes) {
        return mes.equals(mes.toUpperCase());
    }
}
