package ru.holyway.botplatform.skype;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.core.MessageHandler;

/**
 * Created by Sergey on 1/17/2017.
 */
@Component
public class SkypeBot implements Bot {

    @Autowired
    private MessageHandler messageHandler;

    @Value("credential.skype.login")
    private String login;

    @Value("credential.skype.password")
    private String password;

    @Override
    public void init() {
        try {
            Skype skype = new SkypeBuilder(login, password).withAllResources().withExceptionHandler((errorSource, throwable, willShutdown) -> {
                System.out.println("Error: " + errorSource + " " + throwable + " " + willShutdown);
            }).build();
            skype.login();
            skype.subscribe();
            skype.getEventDispatcher().registerListener(new Listener() {
                @EventHandler
                public void onMessage(MessageReceivedEvent e) throws ConnectionException {
                    messageHandler.handleMessage(new SkypeMessageEntity(e));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {

    }
}
