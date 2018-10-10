package ru.holyway.botplatform.telegram.processor;

import org.telegram.telegrambots.meta.bots.AbsSender;

public interface MessagePostLoader {

  void postRun(AbsSender absSender);
}
