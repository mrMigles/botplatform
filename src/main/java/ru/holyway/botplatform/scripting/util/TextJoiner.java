package ru.holyway.botplatform.scripting.util;

import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.entity.AbstractText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class TextJoiner extends AbstractText {

  private List<Object> value = new ArrayList<>();

  public TextJoiner add(final String text) {
    value.add(text);
    return this;
  }

  public TextJoiner add(final Function<ScriptContext, Object> textSupplier) {
    value.add(textSupplier);
    return this;
  }

  public TextJoiner add(final TextJoiner joiner) {
    value.add(joiner.value());
    return this;
  }

  public Function<ScriptContext, String> value() {
    return scriptContext -> {
      final StringBuilder stringBuilder = new StringBuilder();
      for (Object val : value) {
        if (val instanceof String) {
          stringBuilder.append((String) val);
        }
        if (val instanceof Function) {
          final Object value = ((Function<ScriptContext, Object>) val).apply(scriptContext);
          if (value != null) {
            stringBuilder.append(value.toString());
          }
        }
      }
      return stringBuilder.toString();
    };
  }

  public TextJoiner() {
  }

  public static TextJoiner text(final String text) {
    return new TextJoiner().add(text);
  }

  public static TextJoiner text(final Function<ScriptContext, Object> textSupplier) {
    return new TextJoiner().add(textSupplier);
  }

  public static TextJoiner text(final TextJoiner joiner) {
    return new TextJoiner().add(joiner);
  }

  public static Function<ScriptContext, String> random(int start, int end) {
    return scriptContext -> String.valueOf(new Random().nextInt((end - start) + 1) + start);
  }
}
