package ru.holyway.botplatform.core.handler;

import com.sun.jersey.core.util.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.data.DataService;

/**
 * Created by Sergey on 10/12/2017.
 */
@Component
public class AuthenticationHandler implements MessageHandler {

    @Autowired
    private DataService dataService;

    @Override
    public String provideAnswer(MessageEntity messageEntity) {
        final String mes = messageEntity.getText();
        final String chatId = messageEntity.getChatId();
        if (StringUtils.containsIgnoreCase(mes, "authenticate, redirect_uri=")) {
            if (mes.length() > 28) {
                final String redirect = mes.substring(27);
                if (!StringUtils.isEmpty(redirect)) {
                    return "For authenticate please click on the link: \n " + redirect + "?token=" + new String(Base64.encode(chatId + ":" + messageEntity.getSenderName()));
                }
            }
        }
        if (StringUtils.containsIgnoreCase(mes, "get token")) {
            final String token = dataService.getSettings().getToken(chatId);
            dataService.updateSettings();
            return token;
        }
        if (StringUtils.containsIgnoreCase(mes, "revoke token")) {
            final String token = dataService.getSettings().generateNewToken(chatId);
            dataService.updateSettings();
            return token;
        }
        if (StringUtils.containsIgnoreCase(mes, "request user token")) {
            return dataService.getSettings().getUserToken(chatId, messageEntity.getSenderLogin(), messageEntity.getSenderName());
        }

        return null;
    }
}
