package ru.holyway.botplatform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import ru.holyway.botplatform.config.JobInitializer;
import ru.holyway.botplatform.core.Bot;

import javax.annotation.PostConstruct;
import java.util.List;

//@EnableAutoConfiguration
//@EnableAuthorizationServer
@SpringBootApplication
public class BotPlatformApplication {

    @Autowired
    private List<Bot> bots;

    public static void main(String[] args) {
        SpringApplication.run(BotPlatformApplication.class, args);
    }

    @PostConstruct
    private void init() {
        bots.forEach(Bot::init);
    }

    @Bean
    public JobInitializer getGrabberInit() {
        return new JobInitializer();
    }
}
