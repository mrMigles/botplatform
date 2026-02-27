package ru.holyway.botplatform.scripting;

import org.junit.jupiter.api.Test;
import ru.holyway.botplatform.scripting.util.Time;

import static org.junit.jupiter.api.Assertions.*;

public class TimeTest {

  private static final long TOLERANCE_MS = 5000;

  @Test
  public void shouldGetCurrentTimestamp() {
    long before = System.currentTimeMillis();
    Time time = Time.now();
    ScriptContext ctx = new ScriptContext();

    Number value = time.value().apply(ctx);

    assertNotNull(value);
    assertTrue(value.longValue() >= before);
    assertTrue(value.longValue() <= System.currentTimeMillis() + TOLERANCE_MS);
  }

  @Test
  public void shouldAddOneHour() {
    long before = System.currentTimeMillis();
    Time time = Time.now().addHours(1);
    ScriptContext ctx = new ScriptContext();

    Number value = time.value().apply(ctx);
    long expected = before + 3_600_000L;

    assertTrue(Math.abs(value.longValue() - expected) < TOLERANCE_MS);
  }

  @Test
  public void shouldAddNegativeHours() {
    long before = System.currentTimeMillis();
    Time time = Time.now().addHours(-2);
    ScriptContext ctx = new ScriptContext();

    Number value = time.value().apply(ctx);
    long expected = before - 7_200_000L;

    assertTrue(Math.abs(value.longValue() - expected) < TOLERANCE_MS);
  }

  @Test
  public void shouldAddMinutes() {
    long before = System.currentTimeMillis();
    Time time = Time.now().addMinutes(30);
    ScriptContext ctx = new ScriptContext();

    Number value = time.value().apply(ctx);
    long expected = before + 1_800_000L;

    assertTrue(Math.abs(value.longValue() - expected) < TOLERANCE_MS);
  }

  @Test
  public void shouldAddSeconds() {
    long before = System.currentTimeMillis();
    Time time = Time.now().addSeconds(60);
    ScriptContext ctx = new ScriptContext();

    Number value = time.value().apply(ctx);
    long expected = before + 60_000L;

    assertTrue(Math.abs(value.longValue() - expected) < TOLERANCE_MS);
  }

  @Test
  public void shouldAddDays() {
    long before = System.currentTimeMillis();
    Time time = Time.now().addDays(1);
    ScriptContext ctx = new ScriptContext();

    Number value = time.value().apply(ctx);
    long expected = before + 86_400_000L;

    assertTrue(Math.abs(value.longValue() - expected) < TOLERANCE_MS);
  }

  @Test
  public void shouldAddMonths() {
    Time time = Time.now().addMonths(1);
    ScriptContext ctx = new ScriptContext();

    Number value = time.value().apply(ctx);

    assertNotNull(value);
    assertTrue(value.longValue() > System.currentTimeMillis());
  }

  @Test
  public void shouldReturnTrueForAfterWithPastDate() {
    long past = System.currentTimeMillis() - 1_000L;
    Time time = Time.now().addHours(1);
    ScriptContext ctx = new ScriptContext();

    assertTrue(time.after(past).test(ctx));
  }

  @Test
  public void shouldReturnFalseForAfterWithFutureDate() {
    long future = System.currentTimeMillis() + 7_200_000L;
    Time time = Time.now().addHours(1);
    ScriptContext ctx = new ScriptContext();

    assertFalse(time.after(future).test(ctx));
  }

  @Test
  public void shouldReturnTrueForBeforeWithFutureDate() {
    long future = System.currentTimeMillis() + 3_600_000L;
    Time time = Time.now();
    ScriptContext ctx = new ScriptContext();

    assertTrue(time.before(future).test(ctx));
  }

  @Test
  public void shouldReturnFalseForBeforeWithPastDate() {
    long past = System.currentTimeMillis() - 1_000L;
    Time time = Time.now();
    ScriptContext ctx = new ScriptContext();

    assertFalse(time.before(past).test(ctx));
  }

  @Test
  public void shouldFormatTimeAsHHmmss() {
    Time time = Time.now();
    ScriptContext ctx = new ScriptContext();

    String result = time.asString().apply(ctx);

    assertNotNull(result);
    assertTrue(result.matches("\\d{2}:\\d{2}:\\d{2}"),
        "Expected HH:mm:ss format but got: " + result);
  }

  @Test
  public void shouldAddHoursViaFunctionParameter() {
    long before = System.currentTimeMillis();
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("hours", "2");

    Time time = Time.now().addHours(c -> Long.parseLong(c.getContextValue("hours")));
    Number value = time.value().apply(ctx);
    long expected = before + 7_200_000L;

    assertTrue(Math.abs(value.longValue() - expected) < TOLERANCE_MS);
  }

  @Test
  public void shouldAddMinutesViaFunctionParameter() {
    long before = System.currentTimeMillis();
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("mins", "15");

    Time time = Time.now().addMinutes(c -> Long.parseLong(c.getContextValue("mins")));
    Number value = time.value().apply(ctx);
    long expected = before + 900_000L;

    assertTrue(Math.abs(value.longValue() - expected) < TOLERANCE_MS);
  }

  @Test
  public void shouldCheckAfterWithFunctionDate() {
    ScriptContext ctx = new ScriptContext();
    ctx.setContextValue("ts", String.valueOf(System.currentTimeMillis() - 5000));

    Time time = Time.now().addHours(1);

    assertTrue(time.after(c -> Long.parseLong(c.getContextValue("ts"))).test(ctx));
  }
}
