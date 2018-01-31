package ru.holyway.botplatform.core.handler;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;

import javax.annotation.PostConstruct;

/**
 * Created by seiv0814 on 10-10-17.
 */
@Component
public class WikiHandler implements MessageHandler {

    private AIConfiguration configuration;

    private AIDataService dataService;

    @Value("${credential.ai.token}")
    private String apiAiToken;

    @PostConstruct
    public void postConstruct() {
        configuration = new AIConfiguration(apiAiToken);
        dataService = new AIDataService(configuration);
    }


    @Override
    public String provideAnswer(final MessageEntity messageEntity) {
        if (StringUtils.containsIgnoreCase(messageEntity.getText(), "что такое")) {
            try {
                return getAPIAnswer(messageEntity.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getAPIAnswer(String message) throws Exception {
        AIRequest request = new AIRequest(message);

        AIResponse response = dataService.request(request);

        if (response.getStatus().getCode() == 200) {
            return response.getResult().getFulfillment().getSpeech();
        } else {
            throw new Exception("Error code: " + response.getResult().toString());
        }

    }
}
