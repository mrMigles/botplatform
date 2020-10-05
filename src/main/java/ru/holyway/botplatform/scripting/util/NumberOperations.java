package ru.holyway.botplatform.scripting.util;

import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.entity.AbstractNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NumberOperations extends AbstractNumber {

  private List<NumberOperation> objectMap = new ArrayList<>();

  private boolean isLong = true;

  private enum Operation {
    ADDITION, SUBTRACT, DIVIDE, MULTIPLY, MOD
  }

  public NumberOperations add(Number number) {
    objectMap.add(new NumberOperation(Operation.ADDITION, number));
    return this;
  }

  public NumberOperations add(Function<ScriptContext, ?> functionNumber) {
    objectMap.add(new NumberOperation(Operation.ADDITION, functionNumber));
    return this;
  }

  public NumberOperations divide(Number number) {
    objectMap.add(new NumberOperation(Operation.DIVIDE, number));
    return this;
  }

  public NumberOperations divide(Function<ScriptContext, ?> functionNumber) {
    objectMap.add(new NumberOperation(Operation.DIVIDE, functionNumber));
    return this;
  }

  public NumberOperations subtract(Number number) {
    objectMap.add(new NumberOperation(Operation.SUBTRACT, number));
    return this;
  }

  public NumberOperations subtract(Function<ScriptContext, ?> functionNumber) {
    objectMap.add(new NumberOperation(Operation.SUBTRACT, functionNumber));
    return this;
  }

  public NumberOperations multiply(Number number) {
    objectMap.add(new NumberOperation(Operation.MULTIPLY, number));
    return this;
  }

  public NumberOperations multiply(Function<ScriptContext, ?> functionNumber) {
    objectMap.add(new NumberOperation(Operation.MULTIPLY, functionNumber));
    return this;
  }

  public NumberOperations mod(Number number) {
    objectMap.add(new NumberOperation(Operation.MOD, number));
    return this;
  }

  public NumberOperations mod(Function<ScriptContext, ?> functionNumber) {
    objectMap.add(new NumberOperation(Operation.MOD, functionNumber));
    return this;
  }

  private NumberOperations onlyLong(boolean onlyLong) {
    this.isLong = onlyLong;
    return this;
  }


  @Override
  public Function<ScriptContext, Number> value() {
    return scriptContext -> {

      Double result = 0D;
      for (NumberOperation opEntry : objectMap) {
        Double opValue = opEntry.value(scriptContext);
        switch (opEntry.getOperation()) {
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
          case MOD:
            result %= opValue;
            break;
        }
      }
      return isLong ? result.longValue() : result;
    };
  }

  private Function<ScriptContext, Double> convert(Function<ScriptContext, Object> functionNumber) {
    return scriptContext -> Double.parseDouble(functionNumber.apply(scriptContext).toString());
  }

  public static NumberOperations number(Number number) {
    return new NumberOperations().add(number);
  }

  public static NumberOperations number(Function<ScriptContext, Object> stringFunction) {
    return new NumberOperations().add(stringFunction);
  }


  public NumberOperations asDouble() {
    return number(scriptContext -> String.valueOf(value().apply(scriptContext).doubleValue()))
        .onlyLong(false);
  }

  public NumberOperations asLong() {
    return number(scriptContext -> String.valueOf(value().apply(scriptContext).longValue()));
  }

  public Function<ScriptContext, Long> longValue() {
    return scriptContext -> asLong().apply(scriptContext).longValue();
  }

  private static class NumberOperation {
    private Operation operation;
    private Number value;
    private Function<ScriptContext, ?> functionValue;

    public NumberOperation(Operation operation, Number value) {
      this.operation = operation;
      this.value = value;
    }

    public NumberOperation(Operation operation, Function<ScriptContext, ?> functionValue) {
      this.operation = operation;
      this.functionValue = functionValue;
    }

    public Operation getOperation() {
      return operation;
    }

    public Double value(ScriptContext scriptContext) {
      if (value != null) {
        return Double.parseDouble(value.toString());
      } else if (functionValue != null) {
        return Double.parseDouble(functionValue.apply(scriptContext) != null ? functionValue.apply(scriptContext).toString() : "0");
      }
      return null;
    }
  }
}
