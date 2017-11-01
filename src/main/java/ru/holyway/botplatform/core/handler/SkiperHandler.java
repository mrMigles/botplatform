package ru.holyway.botplatform.core.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;
import ru.holyway.botplatform.core.ProcessStopException;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.entity.JSettings;

import javax.annotation.PostConstruct;

/**
 * Created by seiv0814 on 01-11-17.
 */
@Component
@Order(2)
public class SkiperHandler implements MessageHandler {

    private JSettings settings;

    @Autowired
    private DataHelper dataHelper;

    @PostConstruct
    protected void postConstruct() {
        this.settings = dataHelper.getSettings();
    }

    @Override
    public String provideAnswer(MessageEntity messageEntity) {
        if (settings.getMuteChats().contains(messageEntity.getChatId())) {
            throw new ProcessStopException("Chat " + messageEntity.getChatId() + "has been muted");
        }
        return null;
    }
}
