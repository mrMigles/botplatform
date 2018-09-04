package ru.holyway.botplatform.core.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import ru.holyway.botplatform.core.MessageEntity;

/**
 * Created by seiv0814 on 10-10-17.
 */
@Component
public class SimpleQuestionHandler implements MessageHandler {

  private int count = 0;
  private int goodCount = 0;
  private long lastStamp = 0;


  @Override
  public String provideAnswer(final MessageEntity messageEntity) {
    final String mes = messageEntity.getText();
    final String chatId = messageEntity.getChatId();
    if (mes.equalsIgnoreCase("пахом")) {
      return "Что.. что, что случилося то?";
    }

    if (StringUtils.containsIgnoreCase(mes, "Пахом, как дела") || StringUtils
        .containsIgnoreCase(mes, "Пахом, Как дела") || StringUtils
        .containsIgnoreCase(mes, "Пахом, как сам")) {
      return "да как земля";
    }

    if (StringUtils.containsIgnoreCase(mes, "Привет")) {
      return "Здрасти, Дравсвуйте!";
    }
    if (StringUtils.containsIgnoreCase(mes, "скучн") || StringUtils
        .containsIgnoreCase(mes, "он умеет")) {
      return "Хочешь я на одной ноге постою, Как цапля, хочешь?";
    }
    if (StringUtils.containsIgnoreCase(mes, "цапл") || StringUtils
        .containsIgnoreCase(mes, "чайк")) {
      return "курлык-курлык!";
    }
    return null;
  }

}
