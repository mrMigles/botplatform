package ru.holyway.botplatform.scripting;

import org.junit.jupiter.api.Test;
import ru.holyway.botplatform.scripting.entity.TimePredicate;

import static org.junit.jupiter.api.Assertions.*;

public class TimePredicateTest {

  @Test
  public void shouldCreatePredicateFromFiveFieldCron() {
    TimePredicate predicate = TimePredicate.cron("0 * * * *");

    assertNotNull(predicate.getTrigger());
  }

  @Test
  public void shouldCreatePredicateFromSixFieldCron() {
    TimePredicate predicate = TimePredicate.cron("0 0 * * * *");

    assertNotNull(predicate.getTrigger());
  }

  @Test
  public void shouldCreatePredicateFromSevenFieldCron() {
    TimePredicate predicate = TimePredicate.cron("0 30 10 * * *");

    assertNotNull(predicate.getTrigger());
  }

  @Test
  public void shouldCreateEveryMinuteSchedule() {
    TimePredicate predicate = TimePredicate.every("5m");

    assertNotNull(predicate.getTrigger());
  }

  @Test
  public void shouldCreateEveryHourSchedule() {
    TimePredicate predicate = TimePredicate.every("2h");

    assertNotNull(predicate.getTrigger());
  }

  @Test
  public void shouldCreateEveryDaySchedule() {
    TimePredicate predicate = TimePredicate.every("1d");

    assertNotNull(predicate.getTrigger());
  }

  @Test
  public void shouldDefaultToMinutesForUnknownEveryUnit() {
    TimePredicate predicate = TimePredicate.every("3x");

    assertNotNull(predicate.getTrigger());
  }

  @Test
  public void shouldAlwaysReturnFalseFromTest() {
    TimePredicate predicate = TimePredicate.every("1m");

    assertFalse(predicate.test(new ScriptContext()));
  }

  @Test
  public void shouldNormalize6FieldCronByDroppingSeconds() {
    TimePredicate predicate = TimePredicate.cron("30 0 * * * *");

    assertNotNull(predicate.getTrigger());
  }
}
