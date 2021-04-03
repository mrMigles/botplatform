package ru.holyway.botplatform.scripting.entity;

import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Consumer;
import java.util.function.Function;

public class VariableEntity extends AbstractText {

  private Function<ScriptContext, String> varNameFunc;
  private String varName;

  public VariableEntity(Function<ScriptContext, String> varNameFunc) {
    this.varNameFunc = varNameFunc;
  }

  public VariableEntity(String varName) {
    this.varName = varName;
  }

  public Consumer<ScriptContext> set(final Object value) {
    return scriptContext -> scriptContext.setContextValue(getVarName().apply(scriptContext), value.toString());
  }

  public Consumer<ScriptContext> set(final Function<ScriptContext, Object> value) {
    return scriptContext -> scriptContext.setContextValue(getVarName().apply(scriptContext), value.apply(scriptContext).toString());
  }

  private Function<ScriptContext, String> getVarName() {
    return scriptContext -> {
      return varNameFunc != null ? varNameFunc.apply(scriptContext) : varName;
    };
  }

  @Override
  public Function<ScriptContext, String> value() {
    return scriptContext -> scriptContext.getContextValue(getVarName().apply(scriptContext));
  }

  public static VariableEntity var(final String varName) {
    return new VariableEntity(varName);
  }

  public static VariableEntity var(final Function<ScriptContext, String> varNameFunc) {
    return new VariableEntity(varNameFunc);
  }
}
