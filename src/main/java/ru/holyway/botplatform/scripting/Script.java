package ru.holyway.botplatform.scripting;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Script {

  private Predicate<ScriptContext> predicates;
  private Consumer<ScriptContext> function;

  public Script when(Predicate<ScriptContext> predicates) {
    this.predicates = predicates;
    return this;
  }

  public Script then(Consumer<ScriptContext> function) {
    this.function = function;
    return this;
  }

  public boolean check(ScriptContext str) {
    return predicates.test(str);
  }

  public void execute(ScriptContext s) {
    function.accept(s);
  }

  public static Script script() {
    return new Script();
  }
}
