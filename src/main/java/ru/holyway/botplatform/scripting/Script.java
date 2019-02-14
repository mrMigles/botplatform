package ru.holyway.botplatform.scripting;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Script {

  private Predicate<ScriptContext> predicates;
  private Consumer<ScriptContext> function;
  private String stringScript;

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

  public String getStringScript() {
    return stringScript;
  }

  public void setStringScript(String stringScript) {
    this.stringScript = stringScript;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Script script = (Script) o;
    return Objects.equals(stringScript, script.stringScript);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stringScript);
  }

  public static Script script() {
    return new Script();
  }

  public static Predicate<ScriptContext> any() {
    return scriptContext -> true;
  }

  public static Consumer<ScriptContext> sout(Object obj) {
    return scriptContext -> {
      if (obj instanceof Function) {
        System.out
            .println(((Function<ScriptContext, String>) obj).apply(scriptContext));
      } else {
        System.out.println(String.valueOf(obj));
      }
    };
  }
}
