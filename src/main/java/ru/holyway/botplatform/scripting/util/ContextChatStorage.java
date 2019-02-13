package ru.holyway.botplatform.scripting.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import ru.holyway.botplatform.scripting.ScriptContext;

public class ContextChatStorage {

  private Map<String, Map<String, String>> map = new HashMap<>();


  public Consumer<ScriptContext> put(String key, String value) {
    return ctx -> map
        .put(ctx.message.messageEntity.getChatId(), Collections.singletonMap(key, value));
  }

  public Function<ScriptContext, String> get(String key) {
    return ctx -> map
        .get(ctx.message.messageEntity.getChatId()).get(key);
  }
}
