package ru.holyway.botplatform.scripting;

import org.junit.jupiter.api.Test;
import ru.holyway.botplatform.scripting.entity.LoopHandler;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class LoopHandlerTest {

  @Test
  public void shouldLoopExactNumberOfIterations() {
    AtomicInteger counter = new AtomicInteger(0);

    LoopHandler.loop(ctx -> counter.incrementAndGet(), 5).accept(new ScriptContext());

    assertEquals(5, counter.get());
  }

  @Test
  public void shouldCapLoopIterationsAt10() {
    AtomicInteger counter = new AtomicInteger(0);

    LoopHandler.loop(ctx -> counter.incrementAndGet(), 20).accept(new ScriptContext());

    assertEquals(10, counter.get());
  }

  @Test
  public void shouldLoopZeroTimesForZeroIterations() {
    AtomicInteger counter = new AtomicInteger(0);

    LoopHandler.loop(ctx -> counter.incrementAndGet(), 0).accept(new ScriptContext());

    assertEquals(0, counter.get());
  }

  @Test
  public void shouldLoopWithFunctionIterations() {
    AtomicInteger counter = new AtomicInteger(0);
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("count", "3");

    LoopHandler.loop(c -> counter.incrementAndGet(), c -> c.getContextValue("count"))
        .accept(ctx);

    assertEquals(3, counter.get());
  }

  @Test
  public void shouldCapFunctionIterationsAt10() {
    AtomicInteger counter = new AtomicInteger(0);
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("count", "15");

    LoopHandler.loop(c -> counter.incrementAndGet(), c -> c.getContextValue("count"))
        .accept(ctx);

    assertEquals(10, counter.get());
  }

  @Test
  public void shouldRetryConsumerAndSucceedOnFirstAttempt() {
    AtomicInteger counter = new AtomicInteger(0);

    LoopHandler.retry((Consumer<ScriptContext>) (ctx -> counter.incrementAndGet())).accept(new ScriptContext());

    assertEquals(1, counter.get());
  }

  @Test
  public void shouldRetryConsumerAfterSingleFailure() {
    AtomicInteger counter = new AtomicInteger(0);

    LoopHandler.retry((Consumer<ScriptContext>) (ctx -> {
      if (counter.getAndIncrement() < 1) {
        throw new RuntimeException("transient failure");
      }
    })).accept(new ScriptContext());

    assertEquals(2, counter.get());
  }

  @Test
  public void shouldRetryConsumerAndFailAfterMaxAttempts() {
    AtomicInteger counter = new AtomicInteger(0);

    try {
      LoopHandler.retry((Consumer<ScriptContext>) (ctx -> {
        counter.incrementAndGet();
        throw new RuntimeException("persistent failure");
      })).accept(new ScriptContext());
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals(3, counter.get());
    }
  }

  @Test
  public void shouldRetryFunctionAndSucceedOnFirstAttempt() {
    String result = LoopHandler.<String>retry(ctx -> "ok").apply(new ScriptContext());

    assertEquals("ok", result);
  }

  @Test
  public void shouldRetryFunctionAfterSingleFailure() {
    AtomicInteger counter = new AtomicInteger(0);

    String result = LoopHandler.<String>retry(ctx -> {
      if (counter.getAndIncrement() < 1) {
        throw new RuntimeException("transient");
      }
      return "success";
    }).apply(new ScriptContext());

    assertEquals("success", result);
    assertEquals(2, counter.get());
  }

  @Test
  public void shouldRetryFunctionAndFailAfterMaxAttempts() {
    try {
      LoopHandler.retry((Function<ScriptContext, String>) (ctx -> {
        throw new RuntimeException("always fails");
      })).apply(new ScriptContext());
      fail("Expected RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("always fails", e.getMessage());
    }
  }

  @Test
  public void shouldWrapAndSuppressException() {
    LoopHandler.wrap(ctx -> {
      throw new RuntimeException("wrapped error");
    }).accept(new ScriptContext());
  }

  @Test
  public void shouldWrapAndExecuteNormally() {
    AtomicInteger counter = new AtomicInteger(0);

    LoopHandler.wrap(ctx -> counter.set(42)).accept(new ScriptContext());

    assertEquals(42, counter.get());
  }
}
