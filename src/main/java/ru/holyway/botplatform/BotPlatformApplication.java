package ru.holyway.botplatform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import ru.holyway.botplatform.core.Bot;

import jakarta.annotation.PostConstruct;

@EnableMethodSecurity
@EnableCaching
@SpringBootApplication(exclude = {GroovyTemplateAutoConfiguration.class})
public class BotPlatformApplication {

  private final Bot bots;

  @Autowired
  public BotPlatformApplication(Bot bots) {
    this.bots = bots;
  }

  public static void main(String[] args) {
    SpringApplication.run(BotPlatformApplication.class, args);
  }

  @PostConstruct
  public void init() {
    bots.init();
  }
}
