package ru.holyway.botplatform.scripting;

import org.junit.Test;
import ru.holyway.botplatform.scripting.entity.ConditionHandler;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ConditionHandlerTest {

  @Test
  public void shouldExecuteThenBranchWhenConditionIsTrue() {
    AtomicInteger counter = new AtomicInteger(0);

    ConditionHandler handler = ConditionHandler.condition(ctx -> true)
        .then(ctx -> counter.set(1))
        .otherwise(ctx -> counter.set(-1));

    handler.accept(new ScriptContext());

    assertEquals(1, counter.get());
  }

  @Test
  public void shouldExecuteElseBranchWhenConditionIsFalse() {
    AtomicInteger counter = new AtomicInteger(0);

    ConditionHandler handler = ConditionHandler.condition(ctx -> false)
        .then(ctx -> counter.set(1))
        .otherwise(ctx -> counter.set(-1));

    handler.accept(new ScriptContext());

    assertEquals(-1, counter.get());
  }

  @Test
  public void shouldDoNothingWhenNoBranchesConfigured() {
    ConditionHandler handler = ConditionHandler.condition(ctx -> true);

    handler.accept(new ScriptContext());
  }

  @Test
  public void shouldPassContextToThenBranch() {
    ScriptContext ctx = new ScriptContext();

    ConditionHandler handler = ConditionHandler.condition(c -> true)
        .then(c -> c.setContextValue("branch", "then"));

    handler.accept(ctx);

    assertEquals("then", ctx.getContextValue("branch"));
  }

  @Test
  public void shouldPassContextToElseBranch() {
    ScriptContext ctx = new ScriptContext();

    ConditionHandler handler = ConditionHandler.condition(c -> false)
        .otherwise(c -> c.setContextValue("branch", "else"));

    handler.accept(ctx);

    assertEquals("else", ctx.getContextValue("branch"));
  }

  @Test
  public void shouldEvaluateConditionUsingContextValue() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("flag", "yes");
    AtomicInteger counter = new AtomicInteger(0);

    ConditionHandler handler = ConditionHandler.condition(c -> "yes".equals(c.getContextValue("flag")))
        .then(c -> counter.incrementAndGet())
        .otherwise(c -> counter.set(-1));

    handler.accept(ctx);

    assertEquals(1, counter.get());
  }

  @Test
  public void shouldChainMultipleConditionHandlers() {
    ScriptContext ctx = new ScriptContext();
    AtomicInteger counter = new AtomicInteger(0);

    ConditionHandler outer = ConditionHandler.condition(c -> true)
        .then(ConditionHandler.condition(c -> true)
            .then(c -> counter.incrementAndGet()));

    outer.accept(ctx);

    assertEquals(1, counter.get());
  }
}
