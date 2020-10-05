package ru.holyway.botplatform.scripting.entity;

import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.TextJoiner;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractNumber implements Function<ScriptContext, Number> {

  public abstract Function<ScriptContext, Number> value();

  public static class SNumber extends AbstractNumber {

    private Function<ScriptContext, Number> value;

    public SNumber(Function<ScriptContext, Number> value) {
      this.value = value;
    }

    @Override
    public Function<ScriptContext, Number> value() {
      return value;
    }
  }

  @Override
  public Number apply(ScriptContext scriptContext) {
    return value().apply(scriptContext);
  }

  public TextJoiner asString() {
    return TextJoiner.text(scriptContext -> String.valueOf(value().apply(scriptContext)));
  }

  public Predicate<ScriptContext> eq(Function<ScriptContext, Number> number) {
    return ctx -> number.apply(ctx) instanceof Long ? value().apply(ctx).longValue() == (Long) number.apply(ctx) : value().apply(ctx).doubleValue() == (Double) number.apply(ctx);
  }

  public Predicate<ScriptContext> eq(Long number) {
    return ctx -> value().apply(ctx) instanceof Long ? ((Long) value().apply(ctx)).equals(number)
        : (value().apply(ctx).longValue() == number);
  }

  public Predicate<ScriptContext> eq(Double number) {
    return ctx -> value().apply(ctx) instanceof Long ? (value().apply(ctx).doubleValue() == number)
        : ((Double) value().apply(ctx)).equals(number);
  }

  public Predicate<ScriptContext> gt(Function<ScriptContext, Number> number) {
    return ctx -> value().apply(ctx) instanceof Long ? (Long) value().apply(ctx) > (number.apply(ctx) instanceof Long ? (Long) number.apply(ctx) : (Double) number.apply(ctx))
        : (Double) value().apply(ctx) > (number.apply(ctx) instanceof Long ? (Long) number.apply(ctx) : (Double) number.apply(ctx));
  }

  public Predicate<ScriptContext> lt(Function<ScriptContext, Number> number) {
    return ctx -> value().apply(ctx) instanceof Long ? (Long) value().apply(ctx) < (number.apply(ctx) instanceof Long ? (Long) number.apply(ctx) : (Double) number.apply(ctx))
        : (Double) value().apply(ctx) < (number.apply(ctx) instanceof Long ? (Long) number.apply(ctx) : (Double) number.apply(ctx));
  }


  public Predicate<ScriptContext> gt(Long number) {
    return ctx -> value().apply(ctx) instanceof Long ? (Long) value().apply(ctx) > number
        : (Double) value().apply(ctx) > number;
  }

  public Predicate<ScriptContext> lt(Long number) {
    return ctx -> value().apply(ctx) instanceof Long ? (Long) value().apply(ctx) < number
        : (Double) value().apply(ctx) < number;
  }

  public Predicate<ScriptContext> gt(Double number) {
    return ctx -> value().apply(ctx) instanceof Long ? (Long) value().apply(ctx) > number
        : (Double) value().apply(ctx) > number;
  }

  public Predicate<ScriptContext> lt(Double number) {
    return ctx -> value().apply(ctx) instanceof Long ? (Long) value().apply(ctx) < number
        : (Double) value().apply(ctx) < number;
  }

}
