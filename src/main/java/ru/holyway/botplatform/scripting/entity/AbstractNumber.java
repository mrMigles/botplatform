package ru.holyway.botplatform.scripting.entity;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.TextJoiner;

public abstract class AbstractNumber {

  protected abstract Function<ScriptContext, Number> value();

  public TextJoiner asString() {
    return TextJoiner.text(scriptContext -> String.valueOf(value().apply(scriptContext)));
  }

  public Predicate<ScriptContext> eq(Number number) {
    return ctx -> Objects.equals(value().apply(ctx), number);
  }

  public Predicate<ScriptContext> eq(Function<ScriptContext, Number> number) {
    return ctx -> Objects.equals(value().apply(ctx), number.apply(ctx));
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
