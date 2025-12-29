package ru.holyway.botplatform.scripting;

import org.junit.Test;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ScriptTest {

  @Test
  public void shouldBuildAndExecuteScript() {
    ScriptContext context = new ScriptContext();
    AtomicBoolean executed = new AtomicBoolean(false);

    Script script = Script.script("demo")
        .when(ctx -> {
          ctx.setContextValue("checked", "yes");
          return true;
        })
        .then(ctx -> executed.set(true))
        .stopable(false)
        .enabled(false)
        .order(5)
        .owner(10);

    assertEquals("demo", script.getName());
    assertFalse(script.isStopable());
    assertFalse(script.isEnabled());
    assertEquals(5, script.getOrder());
    assertEquals(10, script.getOwner());

    assertTrue(script.check(context));
    assertEquals("yes", context.getContextValue("checked"));
    script.execute(context);
    assertTrue(executed.get());
  }

  @Test
  public void shouldCompareAndCancelScripts() {
    Script first = Script.script("first").order(1);
    Script second = Script.script("second").order(2);
    assertTrue(first.compareTo(second) < 0);

    AtomicBoolean cancelled = new AtomicBoolean(false);
    first.setTrigger(new TestScheduledFuture(cancelled));
    first.cancel();
    assertTrue(cancelled.get());
  }

  @Test
  public void shouldCheckEqualityIgnoringOwner() {
    Script one = Script.script("a");
    Script another = Script.script("a");
    one.setStringScript("Script.script('a').owner(123)");
    another.setStringScript("Script.script('a').owner(456)");

    assertEquals(one, another);
    assertEquals(one.hashCode(), another.hashCode());
  }

  private static class TestScheduledFuture implements ScheduledFuture<Object> {
    private final AtomicBoolean cancelled;

    TestScheduledFuture(AtomicBoolean cancelled) {
      this.cancelled = cancelled;
    }

    @Override
    public long getDelay(TimeUnit unit) {
      return 0;
    }

    @Override
    public int compareTo(Delayed o) {
      return 0;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      cancelled.set(true);
      return true;
    }

    @Override
    public boolean isCancelled() {
      return cancelled.get();
    }

    @Override
    public boolean isDone() {
      return cancelled.get();
    }

    @Override
    public Object get() {
      return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) {
      return null;
    }
  }
}
