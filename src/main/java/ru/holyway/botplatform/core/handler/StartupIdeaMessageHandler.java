package ru.holyway.botplatform.core.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;

/**
 * Created by seiv0814 on 31-01-18.
 */
@Component
public class StartupIdeaMessageHandler implements MessageHandler {

    @Override
    public String provideAnswer(MessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        if (StringUtils.containsIgnoreCase(mes, "Пахом, идея")) {
            return "Да это гавно, а не идея, запоминайте пока сами.";
        }
        return null;
    }
}
