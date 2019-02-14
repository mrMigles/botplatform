package ru.holyway.botplatform.scripting.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.entity.AbstractText;

public class TextJoiner extends AbstractText {

  private List<Object> value = new ArrayList<>();

  public TextJoiner add(final String text) {
    value.add(text);
    return this;
  }

  public TextJoiner add(final Function<ScriptContext, String> textSupplier) {
    value.add(textSupplier);
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
          stringBuilder.append(((Function<ScriptContext, String>) val).apply(scriptContext));
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

  public static TextJoiner text(final Function<ScriptContext, String> textSupplier) {
    return new TextJoiner().add(textSupplier);
  }
}
