package ru.holyway.botplatform.scripting.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Function;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.entity.AbstractNumber;

public class NumberOperations extends AbstractNumber {

  private Map<Operation, Object> objectMap = new HashMap<>();

  private boolean isLong = true;

  private enum Operation {
    ADDITION, SUBTRACT, DIVIDE, MULTIPLY
  }

  public NumberOperations add(Number number) {
    objectMap.put(Operation.ADDITION, number);
    return this;
  }

  public NumberOperations add(Function<ScriptContext, String> functionNumber) {
    objectMap.put(Operation.ADDITION, functionNumber);
    return this;
  }

  public NumberOperations divide(Number number) {
    objectMap.put(Operation.DIVIDE, number);
    return this;
  }

  public NumberOperations divide(Function<ScriptContext, String> functionNumber) {
    objectMap.put(Operation.DIVIDE, functionNumber);
    return this;
  }

  public NumberOperations subtract(Number number) {
    objectMap.put(Operation.SUBTRACT, number);
    return this;
  }

  public NumberOperations subtract(Function<ScriptContext, String> functionNumber) {
    objectMap.put(Operation.SUBTRACT, functionNumber);
    return this;
  }

  public NumberOperations multiply(Number number) {
    objectMap.put(Operation.MULTIPLY, number);
    return this;
  }

  public NumberOperations multiply(Function<ScriptContext, String> functionNumber) {
    objectMap.put(Operation.MULTIPLY, functionNumber);
    return this;
  }

  private NumberOperations onlyLong(boolean onlyLong) {
    this.isLong = onlyLong;
    return this;
  }


  @Override
  protected Function<ScriptContext, Number> value() {
    return scriptContext -> {

      Double result = 0D;
      for (Entry<Operation, Object> opEntry : objectMap.entrySet()) {
        Double opValue = 0D;
        if (opEntry.getValue() instanceof Function) {
          opValue = convert((Function<ScriptContext, String>) opEntry.getValue())
              .apply(scriptContext);
        } else if (opEntry.getValue() instanceof Number) {
          opValue = Double.parseDouble(opEntry.getValue().toString());
        }
        switch (opEntry.getKey()) {
          case ADDITION:
            result += opValue;
            break;
          case DIVIDE:
            result /= opValue;
            break;
          case SUBTRACT:
            result -= opValue;
            break;
          case MULTIPLY:
            result *= opValue;
            break;
        }
      }
      return isLong ? result.longValue() : result;
    };
  }

  private Function<ScriptContext, Double> convert(Function<ScriptContext, String> functionNumber) {
    return scriptContext -> Double.parseDouble(functionNumber.apply(scriptContext));
  }

  public static NumberOperations number(Number number) {
    return new NumberOperations().add(number);
  }

  public static NumberOperations number(Function<ScriptContext, String> stringFunction) {
    return new NumberOperations().add(stringFunction);
  }


  public NumberOperations asDouble() {
    return number(scriptContext -> String.valueOf(value().apply(scriptContext).doubleValue()))
        .onlyLong(false);
  }

  public NumberOperations asLong() {
    return number(scriptContext -> String.valueOf(value().apply(scriptContext).longValue()));
  }
}
