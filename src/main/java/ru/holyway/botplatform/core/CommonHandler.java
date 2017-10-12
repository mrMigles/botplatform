package ru.holyway.botplatform.core;

/**
 * Created by Sergey on 1/17/2017.
 */
public interface CommonHandler {
    /**
     *
     * @param messageEntity
     */
    void handleMessage(MessageEntity messageEntity);

    /**
     *
     * @param messageEntity
     * @return
     */
    String generateAnswer(MessageEntity messageEntity);
}
