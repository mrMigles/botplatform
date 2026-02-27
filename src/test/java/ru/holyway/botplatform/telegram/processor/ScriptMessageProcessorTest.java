package ru.holyway.botplatform.telegram.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TaskScheduler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.core.entity.JSettings;
import ru.holyway.botplatform.scripting.Script;
import ru.holyway.botplatform.scripting.ScriptCompiler;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ScriptMessageProcessorTest {

  private ScriptCompiler scriptCompiler;
  private DataHelper dataHelper;
  private TaskScheduler taskScheduler;
  private JSettings settings;
  private ScriptMessageProcessor processor;

  @BeforeEach
  public void setUp() {
    scriptCompiler = mock(ScriptCompiler.class);
    dataHelper = mock(DataHelper.class);
    taskScheduler = mock(TaskScheduler.class);
    settings = new JSettings();
    when(dataHelper.getSettings()).thenReturn(settings);

    processor = new ScriptMessageProcessor(scriptCompiler, dataHelper, taskScheduler);
  }

  // --- isNeedToHandle ---

  @Test
  public void isNeedToHandle_noScriptsAndNoScriptText_returnsFalse() {
    TelegramMessageEntity entity = mockMessageEntity("chat1", "hello", false);

    assertFalse(processor.isNeedToHandle(entity));
  }

  @Test
  public void isNeedToHandle_textStartsWithScriptKeyword_returnsTrue() {
    TelegramMessageEntity entity = mockMessageEntity("chat1", "script().when(Script.any()).then(Script.nothing())", true);

    assertTrue(processor.isNeedToHandle(entity));
  }

  @Test
  public void isNeedToHandle_scriptsExistForChat_returnsTrue() {
    injectScript("chat1", "script().when(Script.any()).then(Script.nothing()).owner(1)");
    TelegramMessageEntity entity = mockMessageEntity("chat1", "regular message", false);

    assertTrue(processor.isNeedToHandle(entity));
  }

  @Test
  public void isNeedToHandle_scriptsForDifferentChat_returnsFalse() {
    injectScript("chat1", "script().when(Script.any()).then(Script.nothing()).owner(1)");
    TelegramMessageEntity entity = mockMessageEntity("chat2", "regular message", false);

    assertFalse(processor.isNeedToHandle(entity));
  }

  // --- getScripts ---

  @Test
  public void getScripts_unknownChat_returnsEmptyList() {
    List<Script> scripts = processor.getScripts("unknownChat");

    assertNotNull(scripts);
    assertTrue(scripts.isEmpty());
  }

  @Test
  public void getScripts_afterInjecting_returnsCorrectCount() {
    injectScript("chat1", "script().when(Script.any()).then(Script.nothing()).owner(1)");

    assertEquals(1, processor.getScripts("chat1").size());
  }

  // --- clearScripts ---

  @Test
  public void clearScripts_removesAllScriptsForChat() {
    injectScript("chat1", "script().when(Script.any()).then(Script.nothing()).owner(1)");

    processor.clearScripts("chat1");

    assertTrue(processor.getScripts("chat1").isEmpty());
  }

  @Test
  public void clearScripts_updatesSettings() {
    injectScript("chat1", "script().when(Script.any()).then(Script.nothing()).owner(1)");

    processor.clearScripts("chat1");

    verify(dataHelper, atLeastOnce()).updateSettings();
    assertNull(settings.getScripts().get("chat1"));
  }

  // --- removeScript by string ---

  @Test
  public void removeScriptByString_existingScript_returnsTrue() {
    String scriptStr = "script().when(Script.any()).then(Script.nothing()).owner(1)";
    injectScript("chat1", scriptStr);

    boolean result = processor.removeScript("chat1", scriptStr);

    assertTrue(result);
    assertTrue(processor.getScripts("chat1").isEmpty());
  }

  @Test
  public void removeScriptByString_nonExistingScript_returnsFalse() {
    injectScript("chat1", "script().when(Script.any()).then(Script.nothing()).owner(1)");

    boolean result = processor.removeScript("chat1", "nonexistent script");

    assertFalse(result);
    assertEquals(1, processor.getScripts("chat1").size());
  }

  @Test
  public void removeScriptByString_updatesSettings() {
    String scriptStr = "script().when(Script.any()).then(Script.nothing()).owner(1)";
    injectScript("chat1", scriptStr);

    processor.removeScript("chat1", scriptStr);

    verify(dataHelper, atLeastOnce()).updateSettings();
  }

  // --- removeScript by integer hash code ---

  @Test
  public void removeScriptByHashCode_existingScript_returnsTrue() {
    String scriptStr = "script().when(Script.any()).then(Script.nothing()).owner(1)";
    injectScript("chat1", scriptStr);
    int hashCode = processor.getScripts("chat1").get(0).hashCode();

    boolean result = processor.removeScript("chat1", hashCode);

    assertTrue(result);
    assertTrue(processor.getScripts("chat1").isEmpty());
  }

  @Test
  public void removeScriptByHashCode_nonExistingHashCode_returnsFalse() {
    injectScript("chat1", "script().when(Script.any()).then(Script.nothing()).owner(1)");

    boolean result = processor.removeScript("chat1", -999999);

    assertFalse(result);
  }

  // --- getScript by string ---

  @Test
  public void getScriptByString_existingScript_returnsScript() {
    String scriptStr = "script().when(Script.any()).then(Script.nothing()).owner(1)";
    injectScript("chat1", scriptStr);

    Script found = processor.getScript("chat1", scriptStr);

    assertNotNull(found);
    assertEquals(scriptStr, found.getStringScript());
  }

  @Test
  public void getScriptByString_nonExistingScript_returnsNull() {
    injectScript("chat1", "script().when(Script.any()).then(Script.nothing()).owner(1)");

    Script found = processor.getScript("chat1", "nonexistent");

    assertNull(found);
  }

  // --- getScript by hash code ---

  @Test
  public void getScriptByHashCode_existingScript_returnsScript() {
    String scriptStr = "script().when(Script.any()).then(Script.nothing()).owner(1)";
    injectScript("chat1", scriptStr);
    int hashCode = processor.getScripts("chat1").get(0).hashCode();

    Script found = processor.getScript("chat1", hashCode);

    assertNotNull(found);
    assertEquals(scriptStr, found.getStringScript());
  }

  @Test
  public void getScriptByHashCode_nonExistingHashCode_returnsNull() {
    injectScript("chat1", "script().when(Script.any()).then(Script.nothing()).owner(1)");

    Script found = processor.getScript("chat1", -999999);

    assertNull(found);
  }

  // --- Constructor preloads scripts ---

  @Test
  public void constructor_loadsPreExistingScriptsFromSettings() {
    String scriptStr = "script().when(Script.any()).then(Script.nothing()).owner(1)";
    Script compiled = buildScript(scriptStr);
    settings.addScript("chatX", scriptStr);
    when(scriptCompiler.compile(scriptStr)).thenReturn(compiled);

    ScriptMessageProcessor freshProcessor =
        new ScriptMessageProcessor(scriptCompiler, dataHelper, taskScheduler);

    assertEquals(1, freshProcessor.getScripts("chatX").size());
    assertEquals(scriptStr, freshProcessor.getScripts("chatX").get(0).getStringScript());
  }

  @Test
  public void constructor_skipsScriptThatFailsCompilation() {
    settings.addScript("chatX", "invalid_script");
    when(scriptCompiler.compile("invalid_script")).thenThrow(new RuntimeException("compile error"));

    ScriptMessageProcessor freshProcessor =
        new ScriptMessageProcessor(scriptCompiler, dataHelper, taskScheduler);

    assertTrue(freshProcessor.getScripts("chatX").isEmpty());
  }

  // --- process: execute matching script ---

  @Test
  public void process_scriptConditionMatches_executesScript() throws TelegramApiException {
    String chatId = "chat1";
    boolean[] executed = {false};
    Script script = Script.script("exec")
        .when(Script.any())
        .then(ctx -> executed[0] = true);
    script.setStringScript("script().when(Script.any()).then(...).owner(1)");

    injectScriptDirectly("chat1", script);

    TelegramMessageEntity entity = mockMessageEntity(chatId, "trigger", false);

    processor.process(entity);

    assertTrue(executed[0]);
  }

  @Test
  public void process_scriptConditionDoesNotMatch_doesNotExecute() throws TelegramApiException {
    String chatId = "chat1";
    boolean[] executed = {false};
    Script script = Script.script("skip")
        .when(ctx -> false)
        .then(ctx -> executed[0] = true);
    script.setStringScript("script().when(ctx->false).then(...).owner(1)");

    injectScriptDirectly(chatId, script);

    TelegramMessageEntity entity = mockMessageEntity(chatId, "trigger", false);

    processor.process(entity);

    assertFalse(executed[0]);
  }

  @Test
  public void process_stopableScriptStopsProcessingChain() throws TelegramApiException {
    String chatId = "chat1";
    boolean[] secondExecuted = {false};

    Script first = Script.script("first").order(1).when(Script.any()).then(Script.nothing());
    first.setStringScript("s1");

    Script second = Script.script("second").order(2).when(Script.any()).then(ctx -> secondExecuted[0] = true);
    second.setStringScript("s2");

    settings.addScript(chatId, "s1");
    settings.addScript(chatId, "s2");
    when(scriptCompiler.compile("s1")).thenReturn(first);
    when(scriptCompiler.compile("s2")).thenReturn(second);
    processor = new ScriptMessageProcessor(scriptCompiler, dataHelper, taskScheduler);

    TelegramMessageEntity entity = mockMessageEntity(chatId, "trigger", false);

    processor.process(entity);

    assertFalse(secondExecuted[0], "Second script should not execute because first is stopable");
  }

  @Test
  public void process_nonStopableScript_continuesChain() throws TelegramApiException {
    String chatId = "chat1";
    boolean[] secondExecuted = {false};

    Script first = Script.script("first").order(1).when(Script.any()).then(Script.nothing()).stopable(false);
    first.setStringScript("s1");

    Script second = Script.script("second").order(2).when(Script.any()).then(ctx -> secondExecuted[0] = true);
    second.setStringScript("s2");

    settings.addScript(chatId, "s1");
    settings.addScript(chatId, "s2");
    when(scriptCompiler.compile("s1")).thenReturn(first);
    when(scriptCompiler.compile("s2")).thenReturn(second);
    processor = new ScriptMessageProcessor(scriptCompiler, dataHelper, taskScheduler);

    TelegramMessageEntity entity = mockMessageEntity(chatId, "trigger", false);

    processor.process(entity);

    assertTrue(secondExecuted[0], "Second script should execute because first is not stopable");
  }

  @Test
  public void process_addNewScript_scriptIsStoredAndPersisted() throws TelegramApiException {
    String chatId = "chat1";
    String scriptText = "script().when(Script.any()).then(Script.nothing())";

    Script compiled = buildScript(scriptText + ".owner(42)");
    when(scriptCompiler.compile(anyString())).thenReturn(compiled);

    TelegramMessageEntity entity = mockMessageEntityForScriptCreation(chatId, scriptText, 42L);

    processor.process(entity);

    assertEquals(1, processor.getScripts(chatId).size());
    verify(dataHelper, atLeastOnce()).updateSettings();
  }

  @Test
  public void process_addDuplicateScript_notAddedAgain() throws TelegramApiException {
    String chatId = "chat1";
    String scriptText = "script().when(Script.any()).then(Script.nothing())";
    String owned = scriptText + ".owner(42)";

    Script compiled = buildScript(owned);
    when(scriptCompiler.compile(anyString())).thenReturn(compiled);

    TelegramMessageEntity entity = mockMessageEntityForScriptCreation(chatId, scriptText, 42L);

    processor.process(entity);
    processor.process(entity);

    assertEquals(1, processor.getScripts(chatId).size());
  }

  // --- isRegardingCallback ---

  @Test
  public void isRegardingCallback_scriptsExistForChat_returnsTrue() {
    // processor keys scripts by getChatId().toString(), so "1" not "chat1"
    injectScript("1", "script().when(Script.any()).then(Script.nothing()).owner(1)");

    org.telegram.telegrambots.meta.api.objects.CallbackQuery callback =
        mock(org.telegram.telegrambots.meta.api.objects.CallbackQuery.class);
    Message msg = mock(Message.class);
    when(callback.getMessage()).thenReturn(msg);
    when(msg.getChatId()).thenReturn(1L);

    assertTrue(processor.isRegardingCallback(callback));
  }

  @Test
  public void isRegardingCallback_noScriptsForChat_returnsFalse() {
    org.telegram.telegrambots.meta.api.objects.CallbackQuery callback =
        mock(org.telegram.telegrambots.meta.api.objects.CallbackQuery.class);
    Message msg = mock(Message.class);
    when(callback.getMessage()).thenReturn(msg);
    when(msg.getChatId()).thenReturn(9999L);

    assertFalse(processor.isRegardingCallback(callback));
  }

  // --- Helpers ---

  private void injectScript(String chatId, String scriptStr) {
    Script script = buildScript(scriptStr);
    when(scriptCompiler.compile(scriptStr)).thenReturn(script);
    settings.addScript(chatId, scriptStr);

    processor = new ScriptMessageProcessor(scriptCompiler, dataHelper, taskScheduler);
  }

  private void injectScriptDirectly(String chatId, Script script) throws TelegramApiException {
    String scriptText = script.getStringScript();
    when(scriptCompiler.compile(anyString())).thenReturn(script);
    settings.addScript(chatId, scriptText);

    processor = new ScriptMessageProcessor(scriptCompiler, dataHelper, taskScheduler);
  }

  private Script buildScript(String scriptStr) {
    Script script = Script.script("test")
        .when(Script.any())
        .then(Script.nothing());
    script.setStringScript(scriptStr);
    return script;
  }

  private TelegramMessageEntity mockMessageEntity(String chatId, String text, boolean hasText) {
    TelegramMessageEntity entity = mock(TelegramMessageEntity.class);
    Message message = mock(Message.class);
    when(entity.getMessage()).thenReturn(message);
    when(entity.getChatId()).thenReturn(chatId);
    when(entity.getText()).thenReturn(text);
    when(message.hasText()).thenReturn(hasText);
    when(message.getText()).thenReturn(text);
    return entity;
  }

  private TelegramMessageEntity mockMessageEntityForScriptCreation(String chatId, String text, long userId)
      throws TelegramApiException {
    TelegramMessageEntity entity = mock(TelegramMessageEntity.class);
    Message message = mock(Message.class);
    User user = mock(User.class);
    AbsSender sender = mock(AbsSender.class);
    Message sentMessage = mock(Message.class);

    when(entity.getMessage()).thenReturn(message);
    when(entity.getChatId()).thenReturn(chatId);
    when(entity.getText()).thenReturn(text);
    when(entity.getSender()).thenReturn(sender);
    when(message.hasText()).thenReturn(true);
    when(message.getText()).thenReturn(text);
    when(message.getFrom()).thenReturn(user);
    when(message.getMessageId()).thenReturn(1);
    when(user.getId()).thenReturn(userId);
    when(sender.execute(any(SendMessage.class))).thenReturn(sentMessage);
    when(sentMessage.getMessageId()).thenReturn(100);

    return entity;
  }
}
