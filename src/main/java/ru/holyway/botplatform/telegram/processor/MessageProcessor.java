package ru.holyway.botplatform.telegram.processor;

import org.telegram.telegrambots.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

public interface MessageProcessor {

    /**
     * @param messageEntity
     * @return
     */
    boolean isNeedToHandle(final TelegramMessageEntity messageEntity);

    /**
     * @param messageEntity
     */
    void process(final TelegramMessageEntity messageEntity) throws TelegramApiException;

}
