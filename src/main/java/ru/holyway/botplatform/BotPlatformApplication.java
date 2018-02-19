package ru.holyway.botplatform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.holyway.botplatform.config.JobInitializer;
import ru.holyway.botplatform.core.Bot;

import javax.annotation.PostConstruct;
import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
@SpringBootApplication
public class BotPlatformApplication {

    @Autowired
    private List<Bot> bots;

    public static void main(String[] args) {
        ApiContextInitializer.init();
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
