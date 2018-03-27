package ru.holyway.botplatform.skype;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.handler.ErrorHandler;
import com.samczsun.skype4j.exceptions.handler.ErrorSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.core.CommonHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sergey on 1/17/2017.
 */
public class SkypeBotSecond implements Bot {

    @Autowired
    private CommonHandler commonHandler;

    @Value("${credential.skype2.login}")
    private String login;

    @Value("${credential.skype2.password}")
    private String password;

    private Skype skype;

    private long lastInit;


    @Override
    public void init() {
        try {
            if (StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
                SkypeBuilder skypeBuilder = new SkypeBuilder(login, password);
                skypeBuilder.withAllResources();
                skypeBuilder.withExceptionHandler(new ErrorHandler() {
                    @Override
                    public void handle(ErrorSource errorSource, Throwable throwable, boolean willShutdown) {
                        System.out.println("Error: " + errorSource + " " + throwable + " " + willShutdown);
                        if (willShutdown) {
                            if (lastInit < System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(30)) {
                                reInit();
                            }
                        }
                    }
                });
                skype = skypeBuilder.build();
                skype.login();

                skype.getEventDispatcher().registerListener(new Listener() {
                    @EventHandler
                    public void onMessage(MessageReceivedEvent e) throws ConnectionException {
                        commonHandler.handleMessage(new SkypeMessageEntity(e));
                    }
                });
                skype.subscribe();
                lastInit = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reInit() {
        if (skype != null) {
            try {
                skype.logout();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        init();
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
