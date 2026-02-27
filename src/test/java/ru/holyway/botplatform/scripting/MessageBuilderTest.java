package ru.holyway.botplatform.scripting;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.entity.MessageBuilder;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MessageBuilderTest {

  @Test
  public void shouldBuildMessageWithButtons() {
    TelegramMessageEntity messageEntity = Mockito.mock(TelegramMessageEntity.class);
    when(messageEntity.getChatId()).thenReturn("123");

    ScriptContext context = new ScriptContext();
    context.message = new MessageScriptEntity();
    context.message.messageEntity = messageEntity;

    MessageBuilder builder = MessageBuilder.builder("hello")
        .chat("123")
        .button("First", "data1")
        .button(ctx -> "Second", ctx -> "data2");

    SendMessage sendMessage = builder.apply(context);

    assertEquals("123", sendMessage.getChatId());
    assertEquals("hello", sendMessage.getText());
    assertTrue(sendMessage.getReplyMarkup() instanceof InlineKeyboardMarkup);
    InlineKeyboardMarkup markup = (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
    assertEquals(2, markup.getKeyboard().get(0).size());
    assertEquals("First", markup.getKeyboard().get(0).get(0).getText());
    assertEquals("data2", markup.getKeyboard().get(0).get(1).getCallbackData());
  }

  @Test
  public void shouldCleanButtonsWhenRequested() {
    TelegramMessageEntity messageEntity = Mockito.mock(TelegramMessageEntity.class);
    when(messageEntity.getChatId()).thenReturn("999");

    ScriptContext context = new ScriptContext();
    context.message = new MessageScriptEntity();
    context.message.messageEntity = messageEntity;

    MessageBuilder builder = MessageBuilder.builder("clear").button("One", "1").cleanButtons();
    SendMessage sendMessage = builder.apply(context);

    InlineKeyboardMarkup markup = (InlineKeyboardMarkup) sendMessage.getReplyMarkup();
    assertTrue(markup.getKeyboard().isEmpty());
  }
}
