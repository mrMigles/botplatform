package ru.holyway.botplatform.core.handler;

import com.sun.jersey.core.util.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.data.DataHelper;

/**
 * Created by Sergey on 10/12/2017.
 */
@Component
@Order(80)
public class AuthenticationHandler implements MessageHandler {

    @Autowired
    private DataHelper dataHelper;

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
        if (StringUtils.containsIgnoreCase(mes, "get token")) {
            return dataHelper.getSettings().getToken(chatId);
        }
        if (StringUtils.containsIgnoreCase(mes, "revoke token")) {
            return dataHelper.getSettings().generateNewToken(chatId);
        }

        return null;
    }
}
