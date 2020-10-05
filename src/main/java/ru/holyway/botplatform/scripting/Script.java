package ru.holyway.botplatform.scripting;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Script {

  private final String name;
  private Predicate<ScriptContext> predicates;
  private Consumer<ScriptContext> function;
  private boolean isStopable = true;
  private boolean isEnabled = true;
  private String stringScript;
  private ScheduledFuture trigger;

  public Script(String name) {
    this.name = name;
  }

  public static Script script() {
    return new Script(String.valueOf(new Random().nextInt(99999999)));
  }

  public static Script script(final String name) {
    return new Script(name);
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

  public static Consumer<ScriptContext> nothing() {
    return scriptContext -> {
    };
  }

  public String getName() {
    return name;
  }

  public Script when(Predicate<ScriptContext> predicates) {
    this.predicates = predicates;
    return this;
  }

  public Script then(Consumer<ScriptContext> function) {
    this.function = function;
    return this;
  }

  public Script stopable(boolean isStopable) {
    this.isStopable = isStopable;
    return this;
  }

  public Script enabled(boolean isEnabled) {
    this.isEnabled = isEnabled;
    return this;
  }

  public boolean check(ScriptContext str) {
    return predicates.test(str);
  }

  public void execute(ScriptContext s) {
    function.accept(s);
  }

  public boolean isStopable() {
    return isStopable;
  }

  public boolean isEnabled() {
    return isEnabled;
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

  public Predicate<ScriptContext> getPredicates() {
    return predicates;
  }

  public void setTrigger(ScheduledFuture trigger) {
    this.trigger = trigger;
  }

  public void cancel() {
    if (trigger != null) {
      trigger.cancel(true);
    }
  }
}
