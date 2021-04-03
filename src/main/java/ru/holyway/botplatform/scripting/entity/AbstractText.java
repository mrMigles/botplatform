package ru.holyway.botplatform.scripting.entity;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.NumberOperations;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractText implements Function<ScriptContext, String> {

  public abstract Function<ScriptContext, String> value();

  public static class Text extends AbstractText {

    private Function<ScriptContext, String> value;

    public Text(Function<ScriptContext, String> value) {
      this.value = value;
    }

    @Override
    public Function<ScriptContext, String> value() {
      return value;
    }
  }

  @Override
  public String apply(ScriptContext scriptContext) {
    return value().apply(scriptContext);
  }

  public Predicate<ScriptContext> eq(String text) {
    return ctx -> value().apply(ctx).equals(text);
  }

  public Predicate<ScriptContext> eq(Function<ScriptContext, String> text) {
    return ctx -> value().apply(ctx).equals(text.apply(ctx));
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

  public Predicate<ScriptContext> cic(Function<ScriptContext, String> text) {
    return ctx -> StringUtils.containsIgnoreCase(value().apply(ctx), text.apply(ctx));
  }

  public Predicate<ScriptContext> startWith(String text) {
    return ctx -> value().apply(ctx).startsWith(text);
  }

  public Predicate<ScriptContext> matches(String text) {
    return ctx -> {
      ctx.setContextValue("regexp", text);
      return value().apply(ctx).matches(text);
    };
  }

  public NumberOperations asNumber() {
    return new NumberOperations().add(value());
  }

  public AbstractText regexp(final String regexp, final Integer group) {
    return new Text(scriptContext -> {
      scriptContext.setContextValue("regexp", regexp);
      Pattern pattern = Pattern.compile(regexp);

      Matcher m = pattern.matcher(value().apply(scriptContext));
      m.find();
      return m.group(group);
    });
  }

  public AbstractText group(final Integer group) {
    return new Text(scriptContext -> {
      Pattern pattern = Pattern.compile(scriptContext.getContextValue("regexp"));

      Matcher m = pattern.matcher(value().apply(scriptContext));
      m.find();
      return m.group(group);
    });
  }

  public AbstractText split(final String deliniter, final Integer group) {
    return new Text(scriptContext -> {
      return value().apply(scriptContext).split(deliniter)[group];
    });
  }

  public AbstractText replace(String from, String to) {
    return new Text(scriptContext -> value().apply(scriptContext).replace(from, to));
  }

  public AbstractText path(final String path) {
    return new Text(scriptContext -> {
      Object res = JsonPath.read(value().apply(scriptContext), path);
      return String.valueOf(res);
    });
  }

  public AbstractText trim() {
    return new Text(scriptContext -> value().apply(scriptContext).trim());
  }

  public AbstractText replace(Function<ScriptContext, String> from, Function<ScriptContext, String> to) {
    return new Text(scriptContext -> value().apply(scriptContext).replace(from.apply(scriptContext), to.apply(scriptContext)));
  }

  public AbstractText replace(String from, Function<ScriptContext, String> to) {
    return new Text(scriptContext -> value().apply(scriptContext).replace(from, to.apply(scriptContext)));
  }

  public AbstractText replace(Function<ScriptContext, String> from, String to) {
    return new Text(scriptContext -> value().apply(scriptContext).replace(from.apply(scriptContext), to));
  }
}
