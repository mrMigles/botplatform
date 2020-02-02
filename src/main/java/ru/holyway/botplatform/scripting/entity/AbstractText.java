package ru.holyway.botplatform.scripting.entity;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.NumberOperations;

public abstract class AbstractText {

  public abstract Function<ScriptContext, String> value();

  public Predicate<ScriptContext> eq(String text) {
    return ctx -> value().apply(ctx).equals(text);
  }

  public Predicate<ScriptContext> eqic(String text) {
    return ctx -> value().apply(ctx).equalsIgnoreCase(text);
  }

  public Predicate<ScriptContext> contains(String text) {
    return ctx -> value().apply(ctx).contains(text);
  }

  public Predicate<ScriptContext> cic(String text) {
    return ctx -> StringUtils.containsIgnoreCase(value().apply(ctx), text);
  }

  public Predicate<ScriptContext> startWith(String text) {
    return ctx -> value().apply(ctx).startsWith(text);
  }

  public Predicate<ScriptContext> matches(String text) {
    return ctx -> value().apply(ctx).matches(text);
  }

  public NumberOperations asNumber() {
    return new NumberOperations().add(value());
  }

  public Function<ScriptContext, String> regexp(final String regexp, final Integer group) {
    return scriptContext -> {
      Pattern pattern = Pattern.compile(regexp);

      Matcher m = pattern.matcher(value().apply(scriptContext));
      m.find();
      return m.group(group);
    };
  }

  public Function<ScriptContext, String> replace(String from, String to) {
    return scriptContext -> value().apply(scriptContext).replaceAll(from, to);
  }

  public Function<ScriptContext, String> replace(Function<ScriptContext, String> from, Function<ScriptContext, String> to) {
    return scriptContext -> value().apply(scriptContext).replaceAll(from.apply(scriptContext), to.apply(scriptContext));
  }

  public Function<ScriptContext, String> replace(String from, Function<ScriptContext, String> to) {
    return scriptContext -> value().apply(scriptContext).replaceAll(from, to.apply(scriptContext));
  }

  public Function<ScriptContext, String> replace(Function<ScriptContext, String> from, String to) {
    return scriptContext -> value().apply(scriptContext).replaceAll(from.apply(scriptContext), to);
  }
}
