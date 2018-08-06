package ru.holyway.botplatform;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.holyway.botplatform.config.JobInitializer;
import ru.holyway.botplatform.core.Bot;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
@SpringBootApplication
public class BotPlatformApplication {

  private final Bot bots;

  @Autowired
  public BotPlatformApplication(Bot bots) {
    this.bots = bots;
  }

  public static void main(String[] args) {
    ApiContextInitializer.init();
    SpringApplication.run(BotPlatformApplication.class, args);
  }

  @PostConstruct
  private void init() {
    bots.init();
  }
}
