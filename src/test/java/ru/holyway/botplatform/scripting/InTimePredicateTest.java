package ru.holyway.botplatform.scripting;

import org.junit.Test;
import ru.holyway.botplatform.scripting.entity.InTimePredicate;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class InTimePredicateTest {

  @Test
  public void shouldParseSecondsDelay() {
    InTimePredicate predicate = InTimePredicate.in("30s");

    assertEquals(TimeUnit.SECONDS.toMillis(30), predicate.getDelay());
  }

  @Test
  public void shouldParseMinutesDelay() {
    InTimePredicate predicate = InTimePredicate.in("5m");

    assertEquals(TimeUnit.MINUTES.toMillis(5), predicate.getDelay());
  }

  @Test
  public void shouldParseHoursDelay() {
    InTimePredicate predicate = InTimePredicate.in("2h");

    assertEquals(TimeUnit.HOURS.toMillis(2), predicate.getDelay());
  }

  @Test
  public void shouldParseDaysDelay() {
    InTimePredicate predicate = InTimePredicate.in("1d");

    assertEquals(TimeUnit.DAYS.toMillis(1), predicate.getDelay());
  }

  @Test
  public void shouldDefaultToMinutesForUnknownUnit() {
    InTimePredicate predicate = InTimePredicate.in("10x");

    assertEquals(TimeUnit.MINUTES.toMillis(10), predicate.getDelay());
  }

  @Test
  public void shouldAlwaysReturnFalseFromTest() {
    InTimePredicate predicate = InTimePredicate.in("1m");

    assertFalse(predicate.test(new ScriptContext()));
  }

  @Test
  public void shouldHandleLargeSecondValue() {
    InTimePredicate predicate = InTimePredicate.in("3600s");

    assertEquals(TimeUnit.HOURS.toMillis(1), predicate.getDelay());
  }

  @Test
  public void shouldHandleLargeMinuteValue() {
    InTimePredicate predicate = InTimePredicate.in("60m");

    assertEquals(TimeUnit.HOURS.toMillis(1), predicate.getDelay());
  }
}
