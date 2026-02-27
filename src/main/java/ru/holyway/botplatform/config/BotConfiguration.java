package ru.holyway.botplatform.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
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

import javax.net.ssl.SSLContext;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

  @Value("${instaprovider.url}")
  private String instaproviderUrl;

  @Value("${bot.config.threadCount}")
  private Integer threadCount;

  @Value("${bot.config.queueSize}")
  private Integer queueSize;

  @Bean
  public CommonHandler messageHandler() {
    return new CommonMessageHandler();
  }

  @Bean
  public Bot telegramBot() {
    return new TelegramBot(threadCount, queueSize);
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
    return orderedMessageHandlers;
  }

  @Bean
  public TaskScheduler taskScheduler() {
    return new ConcurrentTaskScheduler();
  }

  @Bean(name = "scriptScheduler")
  public TaskScheduler scriptScheduler() {
    return new ConcurrentTaskScheduler();
  }

  @Bean
  @Primary
  public RestTemplate restTemplate()
      throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
    HttpComponentsClientHttpRequestFactory requestFactory = buildRequestFactory();
    return new RestTemplate(requestFactory);
  }

  @NotNull
  private HttpComponentsClientHttpRequestFactory buildRequestFactory()
      throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

    SSLContext sslContext = SSLContexts.custom()
        .loadTrustMaterial(null, acceptingTrustStrategy)
        .build();

    CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
            .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .build())
            .build())
        .build();

    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);
    return requestFactory;
  }

  @Bean("instaproviderTemplate")
  public RestTemplate instaproviderTemplate()
      throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    HttpComponentsClientHttpRequestFactory requestFactory = buildRequestFactory();
    RestTemplate template = new RestTemplate(requestFactory);
    template.setUriTemplateHandler(new DefaultUriBuilderFactory(instaproviderUrl));
    return template;
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

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(1000);
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(2);
    retryTemplate.setRetryPolicy(retryPolicy);

    return retryTemplate;
  }
}
