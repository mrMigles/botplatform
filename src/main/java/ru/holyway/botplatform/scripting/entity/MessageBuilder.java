package ru.holyway.botplatform.scripting.entity;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class MessageBuilder implements Function<ScriptContext, SendMessage> {

  private Function<ScriptContext, String> text;
  private Function<ScriptContext, String> chatId;
  private Map<Function<ScriptContext, String>, Function<ScriptContext, String>> buttons = new HashMap<>();
  private boolean cleanButtons = false;

  @Override
  public SendMessage apply(ScriptContext scriptContext) {
    SendMessage sendMessage = SendMessage.builder().chatId(scriptContext.message.messageEntity.getChatId())
        .text(text.apply(scriptContext)).build();

    if (!buttons.isEmpty()) {
      InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

      List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

      List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

      for (Map.Entry<Function<ScriptContext, String>, Function<ScriptContext, String>> button : buttons.entrySet()) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(button.getKey().apply(scriptContext));
        inlineKeyboardButton.setCallbackData(button.getValue().apply(scriptContext));
        inlineKeyboardButtons.add(inlineKeyboardButton);
      }
      keyboard.add(inlineKeyboardButtons);
      keyboardMarkup.setKeyboard(keyboard);

      sendMessage.setReplyMarkup(keyboardMarkup);
    }
    if (cleanButtons) {
      sendMessage.setReplyMarkup(new InlineKeyboardMarkup());
    }

    return sendMessage;
  }

  public Consumer<ScriptContext> send() {
    return s -> {
      try {
        s.setContextValue("lastMessage", s.message.messageEntity.getSender()
            .execute(apply(s))
            .getMessageId().toString());
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public MessageBuilder button(final String buttonText, final String data) {
    buttons.put(scriptContext -> buttonText, scriptContext -> data);
    return this;
  }

  public MessageBuilder button(final String buttonText, final Function<ScriptContext, String> data) {
    buttons.put(scriptContext -> buttonText, data);
    return this;
  }

  public MessageBuilder button(final Function<ScriptContext, String> buttonText, final String data) {
    buttons.put(buttonText, scriptContext -> data);
    return this;
  }

  public MessageBuilder button(final Function<ScriptContext, String> buttonText, final Function<ScriptContext, String> data) {
    buttons.put(buttonText, data);
    return this;
  }

  public MessageBuilder cleanButtons() {
    this.cleanButtons = true;
    return this;
  }

  public MessageBuilder chat(final String chatId) {
    this.chatId = scriptContext -> chatId;
    return this;
  }

  public MessageBuilder chat(final Function<ScriptContext, String> chatId) {
    this.chatId = chatId;
    return this;
  }

  public static MessageBuilder builder(final String text) {
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.text = scriptContext -> text;
    return messageBuilder;
  }

  public static MessageBuilder builder(final Function<ScriptContext, String> text) {
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.text = text;
    return messageBuilder;
  }

  public static MessageBuilder builder() {
    MessageBuilder messageBuilder = new MessageBuilder();
    messageBuilder.text = scriptContext -> "";
    return messageBuilder;
  }

}
