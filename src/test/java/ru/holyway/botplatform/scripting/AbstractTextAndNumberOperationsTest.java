package ru.holyway.botplatform.scripting;

import org.junit.Test;
import ru.holyway.botplatform.scripting.entity.AbstractText;
import ru.holyway.botplatform.scripting.util.NumberOperations;

import static org.junit.Assert.*;

public class AbstractTextAndNumberOperationsTest {

  private static class SimpleText extends AbstractText {
    @Override
    public java.util.function.Function<ScriptContext, String> value() {
      return ctx -> ctx.getContextValue("text");
    }
  }

  @Test
  public void shouldEvaluateTextPredicatesAndTransforms() {
    ScriptContext context = new ScriptContext();
    context.setContextValue("text", " Hello world ");
    AbstractText text = new SimpleText();

    assertTrue(text.eq(" Hello world ").test(context));
    assertTrue(text.eqic(" hello world ").test(context));
    assertTrue(text.contains("world").test(context));
    assertTrue(text.cic("HELLO").test(context));
    assertTrue(text.startWith(" ").test(context));
    assertTrue(text.trim().eq("Hello world").test(context));

    AbstractText regexGroup = text.regexp("(Hello) (world)", 2);
    assertEquals("world", regexGroup.apply(context));

    context.setContextValue("text", "Hello world");
    assertTrue(text.matches("(Hello) (world)").test(context));
    assertEquals("Hello", text.group(1).apply(context));

    context.setContextValue("text", "a-b-c");
    assertEquals("b", text.split("-", 1).apply(context));
    assertNull(text.split("-", 5).apply(context));

    context.setContextValue("text", "json: {\"value\": 15}");
    AbstractText path = new AbstractText.Text(ctx -> ctx.getContextValue("text").substring(6)).path("$.value");
    assertEquals("15", path.apply(context));
  }

  @Test
  public void shouldPerformNumberOperations() {
    ScriptContext context = new ScriptContext();
    context.setContextValue("number", "5");

    NumberOperations operations = NumberOperations.number(10)
        .add(ctx -> ctx.getContextValue("number"))
        .subtract(3)
        .multiply(2)
        .divide(2)
        .mod(4);

    Number result = operations.value().apply(context);
    assertEquals(1L, result.longValue());
    assertEquals(1.0, operations.asDouble().apply(context));
  }
}
