package ru.holyway.botplatform.scripting.entity;

import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class InTimePredicate implements Predicate<ScriptContext> {

  private long delay;

  public InTimePredicate(long delay) {
    this.delay = delay;
  }

  public static InTimePredicate in(String inTime) {
    String letter = inTime.substring(inTime.length() - 1);
    Integer number = Integer.valueOf(inTime.substring(0, inTime.length() - 1));
    long delay;
    switch (letter) {
      case "s":
        delay = TimeUnit.SECONDS.toMillis(number);
        break;
      case "m":
        delay = TimeUnit.MINUTES.toMillis(number);
        break;
      case "h":
        delay = TimeUnit.HOURS.toMillis(number);
        break;
      case "d":
        delay = TimeUnit.DAYS.toMillis(number);
        break;
      default:
        delay = TimeUnit.MINUTES.toMillis(number);
    }
    return new InTimePredicate(delay);
  }

  @Override
  public boolean test(ScriptContext scriptContext) {
    return false;
  }

  public long getDelay() {
    return delay;
  }
}
