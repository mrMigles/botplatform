package ru.holyway.botplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.telegram.telegrambots.bots.AbsSender;
import ru.holyway.botplatform.core.CommonHandler;
import ru.holyway.botplatform.core.CommonMessageHandler;
import ru.holyway.botplatform.core.Context;
import ru.holyway.botplatform.core.data.DataService;
import ru.holyway.botplatform.core.data.MongoDataService;
import ru.holyway.botplatform.core.data.TelegramDataService;
import ru.holyway.botplatform.core.data.telegram.TelegramRepositoryImp;
import ru.holyway.botplatform.core.handler.MessageHandler;
import ru.holyway.botplatform.security.AnonymousChatTokenSecurityFilter;
import ru.holyway.botplatform.telegram.TelegramBot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergey on 1/17/2017.
 */
@Configuration
public class BotConfiguration {

    @Bean
    public CommonHandler messageHandler() {
        return new CommonMessageHandler();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.data.mongodb.repositories", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DataService dataHelper() {
        return new MongoDataService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.data.mongodb.repositories", name = "enabled", havingValue = "false", matchIfMissing = true)
    public DataService dataService() {
        return new TelegramDataService();
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
    public List<MessageHandler> orderedMessageHandlers(final Map<String, MessageHandler> messageHandlers) {
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

    @Primary
    @Bean
    public RepositoryFactorySupport repositoryFactorySupport(TelegramBot telegramBot){
        return new RepositoryFactorySupport() {
            @Override
            public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> aClass) {
                return null;
            }

            @Override
            protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
                return new TelegramRepositoryImp<Object, String>(telegramBot, "");
            }

            @Override
            protected Class<?> getRepositoryBaseClass(RepositoryMetadata repositoryMetadata) {
                return TelegramRepositoryImp.class;
            }
        };
    }

}
