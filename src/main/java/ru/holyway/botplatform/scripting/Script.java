package ru.holyway.botplatform.scripting;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Script {

  private Predicate<ScriptEntityContext> predicates;
  private Consumer<ScriptEntityContext> function;

  public Script when(Predicate<ScriptEntityContext> predicates) {
    this.predicates = predicates;
    return this;
  }

  public Script then(Consumer<ScriptEntityContext> function) {
    this.function = function;
    return this;
  }

  public boolean check(ScriptEntityContext str) {
    return predicates.test(str);
  }

  public void execute(ScriptEntityContext s) {
    function.accept(s);
  }

  public static Script script() {
    return new Script();
  }
}
