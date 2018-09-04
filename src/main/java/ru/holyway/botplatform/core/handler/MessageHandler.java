package ru.holyway.botplatform.core.handler;

import ru.holyway.botplatform.core.MessageEntity;

/**
 * Created by seiv0814 on 10-10-17.
 */
public interface MessageHandler {

  /**
   * @return
   */
  String provideAnswer(final MessageEntity messageEntity);
}
