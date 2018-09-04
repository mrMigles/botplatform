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
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
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
    return new TelegramBot(botOptions());
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
    orderedMessageHandlers.add(messageHandlers.get("recordsHandler"));
    orderedMessageHandlers.add(messageHandlers.get("wikiHandler"));
    orderedMessageHandlers.add(messageHandlers.get("simpleQuestionHandler"));
    return orderedMessageHandlers;
  }

  @Bean
  public DefaultBotOptions botOptions() {
    if (!org.apache.commons.lang3.StringUtils
        .isEmpty(proxyUser) && !org.apache.commons.lang3.StringUtils.isEmpty(proxyPass)) {
      Authenticator.setDefault(new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
        }
      });
    }

    DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

    if (!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {
      botOptions.setProxyHost(proxyHost);
      botOptions.setProxyPort(Integer.valueOf(proxyPort));
      botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
    }
    return botOptions;
  }
}
