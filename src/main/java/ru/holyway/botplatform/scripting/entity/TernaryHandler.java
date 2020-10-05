package ru.holyway.botplatform.scripting.entity;

import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Function;
import java.util.function.Predicate;

public class TernaryHandler implements Function<ScriptContext, Object> {
  private Function<ScriptContext, Object> thenFunction = scriptContext -> "";
  private Function<ScriptContext, Object> elseFunction = scriptContext -> "";
  private Predicate<ScriptContext> condition;

  public TernaryHandler(Predicate<ScriptContext> condition) {
    this.condition = condition;
  }

  @Override
  public Object apply(ScriptContext scriptContext) {
    if (condition.test(scriptContext)) {
      return thenFunction.apply(scriptContext);
    } else {
      return elseFunction.apply(scriptContext);
    }
  }

  public static TernaryHandler ternary(Predicate<ScriptContext> condition) {
    return new TernaryHandler(condition);
  }

  public TernaryHandler then(Function<ScriptContext, Object> thenFunction) {
    this.thenFunction = thenFunction;
    return this;
  }

  public TernaryHandler otherwise(Function<ScriptContext, Object> elseFunction) {
    this.elseFunction = elseFunction;
    return this;
  }
}
