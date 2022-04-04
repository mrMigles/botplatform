package ru.holyway.botplatform;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import ru.holyway.botplatform.core.Bot;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
@SpringBootApplication
@EnableAutoConfiguration(exclude={GroovyTemplateAutoConfiguration.class})
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
  private void init() {
    bots.init();
  }
}
