package ru.holyway.botplatform.scripting.entity;

import ru.holyway.botplatform.scripting.Script;
import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConditionHandler implements Consumer<ScriptContext> {
  private Consumer<ScriptContext> thenConsumer = Script.nothing();
  private Consumer<ScriptContext> elseConsumer = Script.nothing();
  private Predicate<ScriptContext> condition;

  public ConditionHandler(Predicate<ScriptContext> condition) {
    this.condition = condition;
  }

  @Override
  public void accept(ScriptContext scriptContext) {
    if (condition.test(scriptContext)) {
      thenConsumer.accept(scriptContext);
    } else {
      elseConsumer.accept(scriptContext);
    }
  }

  public static ConditionHandler condition(Predicate<ScriptContext> condition) {
    return new ConditionHandler(condition);
  }

  public ConditionHandler then(Consumer<ScriptContext> thenConsumer) {
    this.thenConsumer = thenConsumer;
    return this;
  }

  public ConditionHandler otherwise(Consumer<ScriptContext> elseConsumer) {
    this.elseConsumer = elseConsumer;
    return this;
  }
}
