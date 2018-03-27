package ru.holyway.botplatform.skype;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.core.CommonHandler;

/**
 * Created by Sergey on 1/17/2017.
 */
public class SkypeBot implements Bot {

    @Autowired
    private CommonHandler commonHandler;

    @Value("${credential.skype.login}")
    private String login;

    @Value("${credential.skype.password}")
    private String password;

    private Skype skype;

    @Override
    public void init() {
        try {
            if (StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
                skype = new SkypeBuilder(login, password).withAllResources().withExceptionHandler((errorSource, throwable, willShutdown) -> {
                    System.out.println("Error: " + errorSource + " " + throwable + " " + willShutdown);
                }).build();
                skype.login();
                skype.subscribe();
                skype.getEventDispatcher().registerListener(new Listener() {
                    @EventHandler
                    public void onMessage(MessageReceivedEvent e) throws ConnectionException {
                        commonHandler.handleMessage(new SkypeMessageEntity(e));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void sendMessage(String text, String chatId) {
        try {
            skype.getChat(chatId).sendMessage(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
