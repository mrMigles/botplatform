package ru.holyway.botplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.holyway.botplatform.core.CommonMessageHandler;
import ru.holyway.botplatform.core.MessageHandler;

/**
 * Created by Sergey on 1/17/2017.
 */
@Configuration
public class BotConfiguration {

    @Bean(name = "messageHandler")
    public MessageHandler messageHandler() {
        return new CommonMessageHandler();
    }

}
