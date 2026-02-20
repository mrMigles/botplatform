package ru.holyway.botplatform.telegram.processor;

import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ScriptManagerProcessorTest {

  private ScriptManagerProcessor managerProcessor;
  private ScriptMessageProcessor scriptMessageProcessor;

  @Before
  public void setUp() throws Exception {
    scriptMessageProcessor = mock(ScriptMessageProcessor.class);
    managerProcessor = new ScriptManagerProcessor();

    Field field = ScriptManagerProcessor.class.getDeclaredField("scriptMessageProcessor");
    field.setAccessible(true);
    field.set(managerProcessor, scriptMessageProcessor);
  }

  // --- isNeedToHandle ---

  @Test
  public void isNeedToHandle_clearCommand_returnsTrue() {
    assertTrue(managerProcessor.isNeedToHandle(mockEntity("/clear", true, false)));
  }

  @Test
  public void isNeedToHandle_listCommand_returnsTrue() {
    assertTrue(managerProcessor.isNeedToHandle(mockEntity("/list", true, false)));
  }

  @Test
  public void isNeedToHandle_logsCommand_returnsTrue() {
    assertTrue(managerProcessor.isNeedToHandle(mockEntity("/logs", true, false)));
  }

  @Test
  public void isNeedToHandle_getCommand_returnsTrue() {
    assertTrue(managerProcessor.isNeedToHandle(mockEntity("/get", true, false)));
  }

  @Test
  public void isNeedToHandle_putCommandWithValidPattern_returnsTrue() {
    assertTrue(managerProcessor.isNeedToHandle(mockEntity("/put \"myKey\" \"myValue\"", true, false)));
  }

  @Test
  public void isNeedToHandle_putCommandWithInvalidPattern_returnsFalse() {
    assertFalse(managerProcessor.isNeedToHandle(mockEntity("/put noQuotes", true, false)));
  }

  @Test
  public void isNeedToHandle_regularText_returnsFalse() {
    assertFalse(managerProcessor.isNeedToHandle(mockEntity("hello world", true, false)));
  }

  @Test
  public void isNeedToHandle_noText_returnsFalse() {
    assertFalse(managerProcessor.isNeedToHandle(mockEntityNoText()));
  }

  @Test
  public void isNeedToHandle_replyToScriptMessage_returnsTrue() {
    assertTrue(managerProcessor.isNeedToHandle(mockEntityWithReply("-", "script().when(...)")));
  }

  @Test
  public void isNeedToHandle_replyToNonScriptMessage_returnsFalse() {
    assertFalse(managerProcessor.isNeedToHandle(mockEntityWithReply("-", "regular message")));
  }

  @Test
  public void isNeedToHandle_clearWithExtraText_returnsTrue() {
    assertTrue(managerProcessor.isNeedToHandle(mockEntity("/clear all", true, false)));
  }

  // --- isRegardingCallback ---

  @Test
  public void isRegardingCallback_scriptPrefixedData_returnsTrue() {
    CallbackQuery callback = mock(CallbackQuery.class);
    when(callback.getData()).thenReturn("script:delete:12345");

    assertTrue(managerProcessor.isRegardingCallback(callback));
  }

  @Test
  public void isRegardingCallback_otherData_returnsFalse() {
    CallbackQuery callback = mock(CallbackQuery.class);
    when(callback.getData()).thenReturn("vote:up:42");

    assertFalse(managerProcessor.isRegardingCallback(callback));
  }

  @Test
  public void isRegardingCallback_emptyData_returnsFalse() {
    CallbackQuery callback = mock(CallbackQuery.class);
    when(callback.getData()).thenReturn("other");

    assertFalse(managerProcessor.isRegardingCallback(callback));
  }

  // --- SECURITY_VALUE_SET_REGEX tests ---

  @Test
  public void securityRegex_validPutCommand_matches() {
    Pattern pattern = Pattern.compile(ScriptManagerProcessor.SECURITY_VALUE_SET_REGEX);

    assertTrue(pattern.matcher("/put \"myKey\" \"myValue\"").matches());
    assertTrue(pattern.matcher("/put \"key with spaces\" \"value with spaces\"").matches());
  }

  @Test
  public void securityRegex_invalidPutCommand_doesNotMatch() {
    Pattern pattern = Pattern.compile(ScriptManagerProcessor.SECURITY_VALUE_SET_REGEX);

    assertFalse(pattern.matcher("/put myKey myValue").matches());
    assertFalse(pattern.matcher("/put \"keyOnly\"").matches());
    assertFalse(pattern.matcher("put \"key\" \"value\"").matches());
  }

  @Test
  public void securityRegex_extractsKeyAndValue() {
    Pattern pattern = Pattern.compile(ScriptManagerProcessor.SECURITY_VALUE_SET_REGEX);
    java.util.regex.Matcher m = pattern.matcher("/put \"testKey\" \"testValue\"");

    assertTrue(m.matches());
    assertEquals("testKey", m.group(4));
    assertEquals("testValue", m.group(8));
  }

  @Test
  public void securityRegex_keyWithSpecialChars_matches() {
    Pattern pattern = Pattern.compile(ScriptManagerProcessor.SECURITY_VALUE_SET_REGEX);
    java.util.regex.Matcher m = pattern.matcher("/put \"api_key\" \"abc-123_XYZ\"");

    assertTrue(m.matches());
    assertEquals("api_key", m.group(4));
    assertEquals("abc-123_XYZ", m.group(8));
  }

  // --- Helpers ---

  private TelegramMessageEntity mockEntity(String text, boolean hasText, boolean isReply) {
    TelegramMessageEntity entity = mock(TelegramMessageEntity.class);
    Message message = mock(Message.class);
    when(entity.getMessage()).thenReturn(message);
    when(message.hasText()).thenReturn(hasText);
    when(message.getText()).thenReturn(text);
    when(message.isReply()).thenReturn(isReply);
    return entity;
  }

  private TelegramMessageEntity mockEntityNoText() {
    TelegramMessageEntity entity = mock(TelegramMessageEntity.class);
    Message message = mock(Message.class);
    when(entity.getMessage()).thenReturn(message);
    when(message.hasText()).thenReturn(false);
    return entity;
  }

  private TelegramMessageEntity mockEntityWithReply(String text, String replyText) {
    TelegramMessageEntity entity = mock(TelegramMessageEntity.class);
    Message message = mock(Message.class);
    Message replyMessage = mock(Message.class);
    when(entity.getMessage()).thenReturn(message);
    when(message.hasText()).thenReturn(true);
    when(message.getText()).thenReturn(text);
    when(message.isReply()).thenReturn(true);
    when(message.getReplyToMessage()).thenReturn(replyMessage);
    when(replyMessage.hasText()).thenReturn(true);
    when(replyMessage.getText()).thenReturn(replyText);
    return entity;
  }
}
