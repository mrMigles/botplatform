package ru.holyway.botplatform.scripting.entity;

import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.TextJoiner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ArrayEntity {

  private Function<ScriptContext, List<String>> array;

  public ArrayEntity(Function<ScriptContext, List<String>> array) {
    this.array = array;
  }

  public Consumer<ScriptContext> forEachFrom(Consumer<ScriptContext> func, Function<ScriptContext, Number> startFrom) {
    return scriptContext -> forEachFrom(func, Integer.parseInt(startFrom.apply(scriptContext).toString())).accept(scriptContext);
  }

  public Consumer<ScriptContext> forEachLast(Consumer<ScriptContext> func, Function<ScriptContext, Number> last) {
    return scriptContext -> forEachLast(func, Integer.parseInt(last.apply(scriptContext).toString())).accept(scriptContext);
  }

  public Consumer<ScriptContext> forEachFrom(Consumer<ScriptContext> func, int startFrom) {
    return scriptContext -> {
      List<String> items = array.apply(scriptContext);
      scriptContext.setContextValue("array", String.join("&_&", items));
      for (int i = startFrom; i < items.size() && i < startFrom + 100; i++) {
        scriptContext.setContextValue("array.item", items.get(i));
        func.accept(scriptContext);
      }
    };
  }

  public Consumer<ScriptContext> forEachLast(Consumer<ScriptContext> func, int last) {
    return scriptContext -> {
      List<String> items = array.apply(scriptContext);
      scriptContext.setContextValue("array", String.join("&_&", items));
      int i = items.size() > Math.min(last, 100) ? items.size() - Math.min(last, 100) : 0;
      for (; i < items.size(); i++) {
        scriptContext.setContextValue("array.item", items.get(i));
        func.accept(scriptContext);
      }
    };
  }

  public Consumer<ScriptContext> forEach(Consumer<ScriptContext> func) {
    return forEachFrom(func, 0);
  }

  public AbstractNumber.SNumber size() {
    return new AbstractNumber.SNumber(scriptContext -> array.apply(scriptContext).size());
  }

  public static TextJoiner item() {
    return TextJoiner.text(scriptContext -> scriptContext.getContextValue("array.item"));
  }


  public static ArrayEntity array(Function<ScriptContext, String> message, String spliterator) {
    return new ArrayEntity(scriptContext -> Arrays.asList(message.apply(scriptContext).split(spliterator)));
  }

  public static ArrayEntity array(String array, String spliterator) {
    return new ArrayEntity(scriptContext -> Arrays.asList(array.split(spliterator)));
  }

  public static ArrayEntity array() {
    return new ArrayEntity(scriptContext -> Arrays.asList(scriptContext.getContextValue("array").split("&_&")));
  }
}
