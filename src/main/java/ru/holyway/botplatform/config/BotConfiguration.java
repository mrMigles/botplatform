package ru.holyway.botplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import ru.holyway.botplatform.core.CommonHandler;
import ru.holyway.botplatform.core.CommonMessageHandler;
import ru.holyway.botplatform.core.Context;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.data.MemoryDataHelper;
import ru.holyway.botplatform.core.data.MongoDataHelper;
import ru.holyway.botplatform.core.handler.MessageHandler;
import ru.holyway.botplatform.security.AnonymousChatTokenSecurityFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergey on 1/17/2017.
 */
@Configuration
public class BotConfiguration {

    @Value("${bot.config.datatype}")
    private String dataType;


    @Bean
    public CommonHandler messageHandler() {
        return new CommonMessageHandler();
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

}
