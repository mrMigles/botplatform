package ru.holyway.botplatform.scripting.util;

import ru.holyway.botplatform.scripting.ScriptContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.Predicate;

public class Time extends NumberOperations {

  private Function<ScriptContext, Date> date;

  private Function<ScriptContext, Date> newDate;

  public Time() {

  }

  public Time hours(Integer hours) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.HOUR_OF_DAY, hours);
      return cal.getTime();
    };
    return this;
  }

  public Time addHours(Integer hours) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.HOUR_OF_DAY, hours);
      return cal.getTime();
    };
    return this;
  }

  public Time addHours(Function<ScriptContext, Long> hours) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.HOUR_OF_DAY, hours.apply(scriptContext).intValue());
      return cal.getTime();
    };
    return this;
  }

  public Time addMinutes(Integer minutes) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.MINUTE, minutes);
      return cal.getTime();
    };
    return this;
  }

  public Time addMinutes(Function<ScriptContext, Long> minutes) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.MINUTE, minutes.apply(scriptContext).intValue());
      return cal.getTime();
    };
    return this;
  }

  public Time addSeconds(Integer seconds) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.SECOND, seconds);
      return cal.getTime();
    };
    return this;
  }

  public Time addSeconds(Function<ScriptContext, Long> seconds) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.SECOND, seconds.apply(scriptContext).intValue());
      return cal.getTime();
    };
    return this;
  }

  public Time addDays(Integer days) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.DAY_OF_YEAR, days);
      return cal.getTime();
    };
    return this;
  }

  public Time addDays(Function<ScriptContext, Long> days) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.DAY_OF_YEAR, days.apply(scriptContext).intValue());
      return cal.getTime();
    };
    return this;
  }

  public Time addMonths(Integer months) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.MONTH, months);
      return cal.getTime();
    };
    return this;
  }

  public Time addMonths(Function<ScriptContext, Long> months) {
    this.newDate = scriptContext -> {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date.apply(scriptContext));
      cal.add(Calendar.MONTH, months.apply(scriptContext).intValue());
      return cal.getTime();
    };
    return this;
  }

  public Predicate<ScriptContext> after(long date) {
    return scriptContext -> this.newDate.apply(scriptContext).after(new Date(date));
  }

  public Predicate<ScriptContext> before(long date) {
    return scriptContext -> this.newDate.apply(scriptContext).before(new Date(date));
  }

  public Predicate<ScriptContext> after(Function<ScriptContext, Long> date) {
    return scriptContext -> this.newDate.apply(scriptContext).after(new Date(date.apply(scriptContext)));
  }

  public Predicate<ScriptContext> before(Function<ScriptContext, Long> date) {
    return scriptContext -> this.newDate.apply(scriptContext).before(new Date(date.apply(scriptContext)));
  }

  public TextJoiner asString() {
    return TextJoiner.text(scriptContext -> {
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      return newDate == null ? dateFormat.format(date.apply(scriptContext)) : dateFormat.format(newDate.apply(scriptContext));
    });
  }

  public Function<ScriptContext, Number> value() {
    return scriptContext -> this.newDate.apply(scriptContext).getTime();
  }


  public static Time now() {
    Time time = new Time();
    time.date = scriptContext -> Calendar.getInstance(TimeZone.getTimeZone("Etc/UTC")).getTime();
    time.newDate = time.date;
    return time;
  }

  @Override
  public Number apply(ScriptContext scriptContext) {
    return value().apply(scriptContext);
  }
}
