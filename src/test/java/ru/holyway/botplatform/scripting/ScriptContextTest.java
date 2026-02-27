package ru.holyway.botplatform.scripting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptContextTest {

  @Test
  public void shouldStoreAndRetrieveContextValue() {
    ScriptContext ctx = new ScriptContext();

    ctx.setContextValue("key", "value");

    assertEquals("value", ctx.getContextValue("key"));
  }

  @Test
  public void shouldReturnNullForMissingKey() {
    ScriptContext ctx = new ScriptContext();

    assertNull(ctx.getContextValue("nonexistent"));
  }

  @Test
  public void shouldOverwriteExistingValue() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("key", "first");
    ctx.setContextValue("key", "second");

    assertEquals("second", ctx.getContextValue("key"));
  }

  @Test
  public void shouldStoreMultipleKeys() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("a", "1");
    ctx.setContextValue("b", "2");
    ctx.setContextValue("c", "3");

    assertEquals("1", ctx.getContextValue("a"));
    assertEquals("2", ctx.getContextValue("b"));
    assertEquals("3", ctx.getContextValue("c"));
  }

  @Test
  public void shouldInitializeWithDefaultMessageAndTelegramEntities() {
    ScriptContext ctx = new ScriptContext();

    assertNotNull(ctx.message);
    assertNotNull(ctx.telegram);
    assertNotNull(ctx.reply);
  }

  @Test
  public void shouldInitializeWithConstructorParameters() {
    Script script = Script.script("test");

    ScriptContext ctx = new ScriptContext(null, null, script);

    assertSame(script, ctx.script);
  }

  @Test
  public void shouldIsolateContextValuesBetweenInstances() {
    ScriptContext ctx1 = new ScriptContext();
    ScriptContext ctx2 = new ScriptContext();

    ctx1.setContextValue("shared", "fromCtx1");
    ctx2.setContextValue("shared", "fromCtx2");

    assertEquals("fromCtx1", ctx1.getContextValue("shared"));
    assertEquals("fromCtx2", ctx2.getContextValue("shared"));
  }
}
