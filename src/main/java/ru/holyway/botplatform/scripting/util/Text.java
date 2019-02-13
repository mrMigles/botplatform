package ru.holyway.botplatform.scripting.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.holyway.botplatform.scripting.ScriptContext;

public class Text {

  private List<Object> value = new ArrayList<>();

  public Text add(final String text) {
    value.add(text);
    return this;
  }

  public Text add(final Function<ScriptContext, String> textSupplier) {
    value.add(textSupplier);
    return this;
  }

  public Function<ScriptContext, String> regexp(final String regexp, final Integer group) {
    return scriptContext -> {
      Pattern pattern = Pattern.compile(regexp);
      Object val = value.get(0);
      if (val instanceof String) {
        Matcher m = pattern.matcher((CharSequence) val);
        return m.group(group);
      }
      if (val instanceof Function) {
        Matcher m = pattern.matcher(((Function<ScriptContext, String>) val).apply(scriptContext));
        return m.group(group);
      }
      return "";
    };
  }

  public Function<ScriptContext, String> value() {
    return scriptContext -> {
      final StringBuilder stringBuilder = new StringBuilder();
      for (Object val : value) {
        if (val instanceof String) {
          stringBuilder.append((String) val);
        }
        if (val instanceof Function) {
          stringBuilder.append(((Function<ScriptContext, String>) val).apply(scriptContext));
        }
      }
      return stringBuilder.toString();
    };
  }

  public Function<ScriptContext, String> get(Integer index) {
    return scriptContext -> {
      final StringBuilder stringBuilder = new StringBuilder();
      Object val = value.get(index);
      if (val instanceof String) {
        stringBuilder.append((String) val);
      }
      if (val instanceof Function) {
        stringBuilder.append(((Function<ScriptContext, String>) val).apply(scriptContext));
      }
      return stringBuilder.toString();
    };
  }

  public Text() {
  }

  public static Text text(final String text) {
    return new Text().add(text);
  }

  public static Text text(final Function<ScriptContext, String> textSupplier) {
    return new Text().add(textSupplier);
  }
}
