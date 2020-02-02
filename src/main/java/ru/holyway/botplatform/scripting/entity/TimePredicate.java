package ru.holyway.botplatform.scripting.entity;

import java.util.function.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.support.CronTrigger;
import ru.holyway.botplatform.scripting.ScriptContext;

public class TimePredicate implements Predicate<ScriptContext> {

  private CronTrigger trigger;

  public TimePredicate(CronTrigger trigger) {
    this.trigger = trigger;
  }

  public static TimePredicate cron(String cron) {
    int numbers = StringUtils.split(cron, " ").length;
    if (numbers == 5) {
      cron = "0 " + cron;
    }
    return new TimePredicate(new CronTrigger(cron));
  }

  public static TimePredicate every(String everyType) {
    String letter = everyType.substring(everyType.length() - 1);
    Integer number = Integer.valueOf(everyType.substring(0, everyType.length() - 1));
    String cron;
    switch (letter) {
      case "s":
        cron = String.format("*/%d * * * * *", number);
        break;
      case "m":
        cron = String.format("0 */%d * * * *", number);
        break;
      case "h":
        cron = String.format("0 0 */%d * * *", number);
        break;
      case "d":
        cron = String.format("* * * */%d * *", number);
        break;
      default:
        cron = String.format("0 */%d * * * *", number);
    }
    return new TimePredicate(new CronTrigger(cron));
  }

  public CronTrigger getTrigger() {
    return trigger;
  }

  @Override
  public boolean test(ScriptContext scriptContext) {
    return false;
  }


}
