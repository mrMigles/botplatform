package ru.holyway.botplatform.scripting;

import org.junit.jupiter.api.Test;
import ru.holyway.botplatform.scripting.util.TextJoiner;

import static org.junit.jupiter.api.Assertions.*;

public class TextJoinerTest {

  @Test
  public void shouldJoinStaticStrings() {
    TextJoiner joiner = TextJoiner.text("Hello").add(", ").add("World");

    assertEquals("Hello, World", joiner.apply(new ScriptContext()));
  }

  @Test
  public void shouldJoinWithFunctionValue() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("name", "Alice");

    TextJoiner joiner = TextJoiner.text("Hi ").add(c -> c.getContextValue("name"));

    assertEquals("Hi Alice", joiner.apply(ctx));
  }

  @Test
  public void shouldJoinWithNestedTextJoiner() {
    TextJoiner inner = TextJoiner.text("inner");
    TextJoiner outer = TextJoiner.text("outer-").add(inner);

    assertEquals("outer-inner", outer.apply(new ScriptContext()));
  }

  @Test
  public void shouldSkipNullFunctionValues() {
    TextJoiner joiner = TextJoiner.text("prefix").add(ctx -> null);

    assertEquals("prefix", joiner.apply(new ScriptContext()));
  }

  @Test
  public void shouldCreateFromFunctionUsingStaticFactory() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("val", "dynamic");

    TextJoiner joiner = TextJoiner.text(c -> c.getContextValue("val"));

    assertEquals("dynamic", joiner.apply(ctx));
  }

  @Test
  public void shouldCreateFromAnotherJoinerUsingStaticFactory() {
    TextJoiner inner = TextJoiner.text("base");
    TextJoiner outer = TextJoiner.text(inner);

    assertEquals("base", outer.apply(new ScriptContext()));
  }

  @Test
  public void shouldGenerateRandomNumberInRange() {
    ScriptContext ctx = new ScriptContext();

    for (int i = 0; i < 30; i++) {
      int value = Integer.parseInt(TextJoiner.random(5, 10).apply(ctx));
      assertTrue(value >= 5 && value <= 10, "Expected " + value + " to be in range [5,10]");
    }
  }

  @Test
  public void shouldChainMultipleAddCalls() {
    TextJoiner joiner = new TextJoiner()
        .add("a")
        .add("b")
        .add("c")
        .add(ctx -> "d");

    assertEquals("abcd", joiner.apply(new ScriptContext()));
  }

  @Test
  public void shouldSupportTextPredicatesFromAbstractText() {
    ScriptContext ctx = new ScriptContext();
    TextJoiner joiner = TextJoiner.text("hello world");

    assertTrue(joiner.contains("world").test(ctx));
    assertTrue(joiner.startWith("hello").test(ctx));
    assertTrue(joiner.eq("hello world").test(ctx));
    assertFalse(joiner.eq("other").test(ctx));
  }
}
