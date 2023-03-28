package ru.holyway.botplatform.telegram.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class EventMessageProcessor implements MessageProcessor {

  private final TaskScheduler scheduler = new ConcurrentTaskScheduler();

  private Set<String> teaSet = new CopyOnWriteArraySet<>();

  @Override
  public boolean isNeedToHandle(TelegramMessageEntity messageEntity) {
    final String mes = messageEntity.getText();
    if (StringUtils.equals(mes, "/event") || StringUtils.startsWithIgnoreCase(mes, "Пахом, зови")
        || StringUtils.equals(mes, "/go")) {
      return true;
    }
    return false;
  }

  @Override
  public void process(TelegramMessageEntity messageEntity) throws TelegramApiException {
    SendMessage message = new SendMessage();
    message.setChatId(messageEntity.getChatId());
    message.setText("ГО?");

    // Create ReplyKeyboardMarkup object
    InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

    // Create the keyboard (list of keyboard rows)
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    // Create a keyboard row

    List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

    InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
    inlineKeyboardButton.setText("Да");
    inlineKeyboardButton.setCallbackData("Yes");

    inlineKeyboardButtons.add(inlineKeyboardButton);

    inlineKeyboardButton = new InlineKeyboardButton();
    inlineKeyboardButton.setText("Нет");
    inlineKeyboardButton.setCallbackData("No");

    inlineKeyboardButtons.add(inlineKeyboardButton);
    keyboard.add(inlineKeyboardButtons);
    keyboardMarkup.setKeyboard(keyboard);

    //keyboardMarkup.setKeyboard(keyboard);
    // Add it to the entity
    message.setReplyMarkup(keyboardMarkup);

    messageEntity.getSender().execute(message);

  }

  @Override
  public boolean isRegardingCallback(CallbackQuery callbackQuery) {
    return false;
  }

  @Override
  public void processCallBack(CallbackQuery callbackQuery, AbsSender sender)
      throws TelegramApiException {

  }
}
