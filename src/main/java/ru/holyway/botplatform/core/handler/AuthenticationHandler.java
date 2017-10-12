package ru.holyway.botplatform.core.handler;

import com.sun.jersey.core.util.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;

import java.util.Arrays;

/**
 * Created by Sergey on 10/12/2017.
 */
@Component
@Order(7)
public class AuthenticationHandler implements MessageHandler {

    @Override
    public String provideAnswer(MessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        final String chatId = messageEntity.getChatId();
        if (StringUtils.containsIgnoreCase(mes, "authenticate, redirect_uri=")) {
            if (mes.length() > 28) {
                final String redirect = mes.substring(27);
                if (!StringUtils.isEmpty(redirect)) {
                    return "For authenticate please click on the link: \n " + redirect + "?token=" + new String(Base64.encode(chatId + ":" + messageEntity.getSender()));
                }
            }
        }
        return null;
    }
}
