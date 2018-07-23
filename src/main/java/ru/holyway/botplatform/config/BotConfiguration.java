package ru.holyway.botplatform.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import ru.holyway.botplatform.core.Bot;
import ru.holyway.botplatform.core.CommonHandler;
import ru.holyway.botplatform.core.CommonMessageHandler;
import ru.holyway.botplatform.core.Context;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.data.MemoryDataHelper;
import ru.holyway.botplatform.core.data.MongoDataHelper;
import ru.holyway.botplatform.core.handler.MessageHandler;
import ru.holyway.botplatform.security.AnonymousChatTokenSecurityFilter;
import ru.holyway.botplatform.telegram.TelegramBot;

/**
 * Created by Sergey on 1/17/2017.
 */
@Configuration
public class BotConfiguration {

    @Value("${bot.config.datatype}")
    private String dataType;

  @Value("${proxy.config.host}")
  private String proxyHost;

  @Value("${proxy.config.port}")
  private String proxyPort;

  @Value("${proxy.config.user}")
  private String proxyUser;

  @Value("${proxy.config.pass}")
  private String proxyPass;


  @Bean
  public CommonHandler messageHandler() {
    return new CommonMessageHandler();
  }

  @Bean
  public Bot telegramBot() {
    if (StringUtils.isNotEmpty(proxyHost) && StringUtils.isNotEmpty(proxyPort)) {
      setProxy(proxyHost, proxyPort, proxyUser, proxyPass);
    }
    return new TelegramBot();
  }

  @Bean
  public DataHelper dataHelper() {
    if (dataType != null && dataType.equalsIgnoreCase("memory")) {
      return new MemoryDataHelper();
    }
    return new MongoDataHelper();
  }

  @Bean
  public Context context() {
    return new Context();
  }

  @Bean
  public AnonymousAuthenticationFilter anonymousAuthenticationFilter() {
    return new AnonymousChatTokenSecurityFilter("CHAT_TOKEN_FILTER");
  }

  @Bean
  public List<MessageHandler> orderedMessageHandlers(
      final Map<String, MessageHandler> messageHandlers) {
    final List<MessageHandler> orderedMessageHandlers = new ArrayList<>();
    orderedMessageHandlers.add(messageHandlers.get("settingsHandler"));
    orderedMessageHandlers.add(messageHandlers.get("authenticationHandler"));
    orderedMessageHandlers.add(messageHandlers.get("skiperHandler"));
    orderedMessageHandlers.add(messageHandlers.get("messageAnalyzerHandler"));
    orderedMessageHandlers.add(messageHandlers.get("educationHandler"));
    orderedMessageHandlers.add(messageHandlers.get("integrationHandler"));
    orderedMessageHandlers.add(messageHandlers.get("recordsHandler"));
    orderedMessageHandlers.add(messageHandlers.get("wikiHandler"));
    orderedMessageHandlers.add(messageHandlers.get("startupIdeaMessageHandler"));
    orderedMessageHandlers.add(messageHandlers.get("logicalAnswerHandler"));
    orderedMessageHandlers.add(messageHandlers.get("simpleQuestionHandler"));
    return orderedMessageHandlers;
  }

  private void setProxy(final String host, final String port, final String user,
      final String pass) {
    System.setProperty("socksProxyHost", host);
    System.setProperty("socksProxyPort", port);
    if (StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(pass)) {
      System.setProperty("java.net.socks.username", user);
      System.setProperty("java.net.socks.password", pass);
    }

    Authenticator.setDefault(new ProxyAuth(user, pass));
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
