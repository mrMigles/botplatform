package ru.holyway.botplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.holyway.botplatform.core.CommonHandler;
import ru.holyway.botplatform.core.CommonMessageHandler;
import ru.holyway.botplatform.core.Context;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.data.MemoryDataHelper;
import ru.holyway.botplatform.core.data.MongoDataHelper;

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

}
