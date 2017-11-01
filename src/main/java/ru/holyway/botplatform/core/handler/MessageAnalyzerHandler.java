package ru.holyway.botplatform.core.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.Context;
import ru.holyway.botplatform.core.MessageEntity;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by seiv0814 on 01-11-17.
 */
@Component
@Order(3)
public class MessageAnalyzerHandler implements MessageHandler {

    @Autowired
    private Context context;

    @Override
    public String provideAnswer(MessageEntity messageEntity) {

        if (StringUtils.containsIgnoreCase(messageEntity.getText(), "Пахом, анализ")) {
            return sendAnalize(messageEntity);
        }

        if (isJock(messageEntity)) {
            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - context.getLastStamp()) < 10) {
                context.incrementGoodCount();
                context.setLastStamp(0);
            }
            if (new Random().nextInt(100) > 85) {
                context.setLastStamp(0);
                return "\uD83D\uDE04";
            }
            context.setLastStamp(0);
        }

        return null;
    }

    private String sendAnalize(MessageEntity message) {
        final String text = "Анализ использования:\nОтправлено сообщений: " + context.getCount() + "\nУдачных шуток: " + context.getGoodCount() + "\nШуток про Дениcа: " + context.getDenisCount() + "\nСамый поехавший: я";
        return text;
    }

    private boolean isJock(MessageEntity message) {
        if (message.getText() != null && (message.getText().contains("\uD83D\uDE04") || message.getText().contains("\uD83D\uDE03")
                || message.getText().contains("xD") || message.getText().contains(":D") || message.getText().contains("хах")
                || message.getText().contains("лол") || message.getText().contains("lol") || message.getText().contains("Лол"))) {
            return true;
        }
        return false;
    }
}
