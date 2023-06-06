package ru.holyway.botplatform.scripting.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Consumer;
import java.util.function.Function;

public class LoopHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoopHandler.class);


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

  public static <T> Function<ScriptContext, T> retry(Function<ScriptContext, T> function) {
    return ctx -> {
      int attempts = 0;
      while (attempts < 3) {
        try {
          return function.apply(ctx);
        } catch (Throwable e) {
          LOGGER.warn("Attempt {} to execute function {} failed", attempts, function, e);
          attempts++;
          if (attempts == 3) {
            throw e;
          }
          try {
            Thread.sleep(200);
          } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
          }
        }
      }
      throw new RuntimeException("Should not reach here");
    };
  }

  public static Consumer<ScriptContext> retry(Consumer<ScriptContext> function) {
    return ctx -> {
      int attempts = 0;
      while (attempts < 3) {
        try {
          function.accept(ctx);
          return;
        } catch (Throwable e) {
          LOGGER.warn("Attempt {} to execute function {} failed", attempts, function, e);
          attempts++;
          if (attempts == 3) {
            throw e;
          }
          try {
            Thread.sleep(200);
          } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
          }
        }
      }
      throw new RuntimeException("Should not reach here");
    };
  }

  public static Consumer<ScriptContext> wrap(Consumer<ScriptContext> function) {
    return ctx -> {
      try {
        function.accept(ctx);
      } catch (Throwable e) {
        LOGGER.warn("Error execute function {} failed, wrap it verbose", function, e);
      }
    };
  }
}
