package ru.holyway.botplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.holyway.botplatform.core.FileSettings;
import ru.holyway.botplatform.core.MessageHandler;
import ru.holyway.botplatform.core.Settings;
import ru.holyway.botplatform.skype.SkypeMessageHandler;

/**
 * Created by Sergey on 1/17/2017.
 */
@Configuration
public class BotConfiguration {

    @Bean(name = "skypeMessageHandler")
    public MessageHandler skypeMessageHandler(){
        return new SkypeMessageHandler();
    }

    @Bean(name = "telegramMessageHandler")
    public MessageHandler telegramMessageHandler(){
        return new SkypeMessageHandler();
    }

    @Bean
    public Settings settings(){
        return new FileSettings();
    }

}
