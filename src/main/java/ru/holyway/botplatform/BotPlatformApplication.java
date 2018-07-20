package ru.holyway.botplatform;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
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

  @Autowired
  private List<Bot> bots;

  public static void main(String[] args) {
    setProxy();
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

  public static void setProxy() {

    System.setProperty("socksProxyHost", "185.246.153.31");
    System.setProperty("socksProxyPort", "443");
    System.setProperty("java.net.socks.username", "guest");
    System.setProperty("java.net.socks.password", "rnk_go_away");
    Authenticator.setDefault(new ProxyAuth("guest", "rnk_go_away"));


  }

  public static class ProxyAuth extends Authenticator {

    private PasswordAuthentication auth;

    private ProxyAuth(String user, String password) {
      auth = new PasswordAuthentication(user,
          password == null ? new char[]{} : password.toCharArray());
    }

    protected PasswordAuthentication getPasswordAuthentication() {
      return auth;
    }
  }
}
