package ru.holyway.botplatform.scripting;

import org.junit.Test;
import ru.holyway.botplatform.scripting.entity.TernaryHandler;

import static org.junit.Assert.*;

public class TernaryHandlerTest {

  @Test
  public void shouldReturnThenValueWhenConditionIsTrue() {
    TernaryHandler handler = TernaryHandler.ternary(ctx -> true)
        .then(ctx -> "then")
        .otherwise(ctx -> "else");

    assertEquals("then", handler.apply(new ScriptContext()));
  }

  @Test
  public void shouldReturnElseValueWhenConditionIsFalse() {
    TernaryHandler handler = TernaryHandler.ternary(ctx -> false)
        .then(ctx -> "then")
        .otherwise(ctx -> "else");

    assertEquals("else", handler.apply(new ScriptContext()));
  }

  @Test
  public void shouldReturnEmptyStringWhenNoThenConfigured() {
    TernaryHandler handler = TernaryHandler.ternary(ctx -> true);

    assertEquals("", handler.apply(new ScriptContext()));
  }

  @Test
  public void shouldReturnEmptyStringWhenNoElseConfigured() {
    TernaryHandler handler = TernaryHandler.ternary(ctx -> false);

    assertEquals("", handler.apply(new ScriptContext()));
  }

  @Test
  public void shouldEvaluateConditionFromContext() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("flag", "on");

    TernaryHandler handler = TernaryHandler.ternary(c -> "on".equals(c.getContextValue("flag")))
        .then(c -> "active")
        .otherwise(c -> "inactive");

    assertEquals("active", handler.apply(ctx));
  }

  @Test
  public void shouldReturnComplexObjectFromThenBranch() {
    TernaryHandler handler = TernaryHandler.ternary(ctx -> true)
        .then(ctx -> 42)
        .otherwise(ctx -> 0);

    assertEquals(42, handler.apply(new ScriptContext()));
  }

  @Test
  public void shouldReturnContextDependentValue() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("num", "5");

    TernaryHandler handler = TernaryHandler.ternary(c -> Integer.parseInt(c.getContextValue("num")) > 3)
        .then(c -> "big: " + c.getContextValue("num"))
        .otherwise(c -> "small: " + c.getContextValue("num"));

    assertEquals("big: 5", handler.apply(ctx));
  }
}
