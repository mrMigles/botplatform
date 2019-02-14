package ru.holyway.botplatform.scripting.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import ru.holyway.botplatform.scripting.ScriptContext;

public class Time {

  private Supplier<Date> date;

  private Supplier<Date> newDate;

  public Time() {

  }

  public Time hours(Integer hours) {
    this.newDate = () -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.get());
      cal.add(Calendar.HOUR_OF_DAY, hours);
      return cal.getTime();
    };
    return this;
  }

  public Predicate<ScriptContext> after(long date) {
    return scriptContext -> this.newDate.get().after(new Date(date));
  }

  public Predicate<ScriptContext> before(long date) {
    return scriptContext -> this.newDate.get().before(new Date(date));
  }

  public Function<ScriptContext, Long> asLong() {
    return scriptContext -> this.newDate.get().getTime();
  }

  public Function<ScriptContext, String> value() {
    return scriptContext -> {
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      return newDate == null ? dateFormat.format(date.get()) : dateFormat.format(newDate.get());
    };
  }


  public static Time now() {
    Time time = new Time();
    time.date = () -> Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC")).getTime();
    time.newDate = time.date;
    return time;
  }
}
