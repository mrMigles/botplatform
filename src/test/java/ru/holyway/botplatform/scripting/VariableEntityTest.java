package ru.holyway.botplatform.scripting;

import org.junit.Test;
import ru.holyway.botplatform.scripting.entity.VariableEntity;

import static org.junit.Assert.*;

public class VariableEntityTest {

  @Test
  public void shouldSetAndGetVariable() {
    ScriptContext ctx = new ScriptContext();
    VariableEntity var = VariableEntity.var("myVar");

    var.set("hello").accept(ctx);

    assertEquals("hello", var.apply(ctx));
  }

  @Test
  public void shouldSetVariableWithFunctionValue() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("source", "world");
    VariableEntity var = VariableEntity.var("target");

    var.set(c -> c.getContextValue("source")).accept(ctx);

    assertEquals("world", ctx.getContextValue("target"));
  }

  @Test
  public void shouldReturnNullForUnsetVariable() {
    ScriptContext ctx = new ScriptContext();
    VariableEntity var = VariableEntity.var("nonExistent");

    assertNull(var.apply(ctx));
  }

  @Test
  public void shouldSupportDynamicVariableName() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("key", "dynVar");

    VariableEntity var = VariableEntity.var(c -> c.getContextValue("key"));
    var.set("value").accept(ctx);

    assertEquals("value", ctx.getContextValue("dynVar"));
  }

  @Test
  public void shouldOverwriteExistingVariable() {
    ScriptContext ctx = new ScriptContext();
    VariableEntity var = VariableEntity.var("x");

    var.set("first").accept(ctx);
    var.set("second").accept(ctx);

    assertEquals("second", var.apply(ctx));
  }

  @Test
  public void shouldConvertNumericValueToString() {
    ScriptContext ctx = new ScriptContext();
    VariableEntity var = VariableEntity.var("num");

    var.set(42).accept(ctx);

    assertEquals("42", var.apply(ctx));
  }

  @Test
  public void shouldSetVariableWithDynamicNameAndFunctionValue() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("varName", "result");
    ctx.setContextValue("input", "computed");

    VariableEntity var = VariableEntity.var(c -> c.getContextValue("varName"));
    var.set(c -> c.getContextValue("input")).accept(ctx);

    assertEquals("computed", ctx.getContextValue("result"));
  }

  @Test
  public void shouldApplyTextPredicatesOnVariableValue() {
    ScriptContext ctx = new ScriptContext();
    VariableEntity var = VariableEntity.var("greeting");

    var.set("hello").accept(ctx);

    assertTrue(var.eq("hello").test(ctx));
    assertTrue(var.contains("ell").test(ctx));
    assertFalse(var.eq("world").test(ctx));
  }
}
