package ru.holyway.botplatform.scripting.entity;

import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Consumer;
import java.util.function.Function;

public class LoopHandler {

  public static Consumer<ScriptContext> loop(Consumer<ScriptContext> function, Integer iterations) {
    return ctr -> {
      int iter = Math.min(iterations, 10);
      for (int i = 0; i < iter; i++) {
        function.accept(ctr);
      }
    };
  }

  public static Consumer<ScriptContext> loop(Consumer<ScriptContext> function, Function<ScriptContext, String> iterations) {
    return ctr -> {
      int iter = Integer.parseInt(iterations.apply(ctr));
      iter = Math.min(iter, 10);
      for (int i = 0; i < iter; i++) {
        function.accept(ctr);
      }
    };
  }
}
