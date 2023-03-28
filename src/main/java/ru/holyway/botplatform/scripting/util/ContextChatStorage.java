package ru.holyway.botplatform.scripting.util;

import ru.holyway.botplatform.core.data.DataHelper;
import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Consumer;
import java.util.function.Function;

public class ContextChatStorage {

  private final DataHelper dataHelper;

  public ContextChatStorage(final DataHelper dataHelper) {
    this.dataHelper = dataHelper;
  }

  public Consumer<ScriptContext> put(String key, String value) {
    return ctx -> dataHelper.putToScriptMap(ctx.message.messageEntity.getChatId(), key, value);
  }

  public Consumer<ScriptContext> put(String key, Function<ScriptContext, Object> functionValue) {
    return ctx -> dataHelper.putToScriptMap(ctx.message.messageEntity.getChatId(), key, functionValue.apply(ctx));
  }

  public Consumer<ScriptContext> put(Function<ScriptContext, Object> key, Function<ScriptContext, Object> functionValue) {
    return ctx -> dataHelper.putToScriptMap(ctx.message.messageEntity.getChatId(), key.apply(ctx), functionValue.apply(ctx));
  }

  public Consumer<ScriptContext> put(Function<ScriptContext, Object> key, String value) {
    return ctx -> dataHelper.putToScriptMap(ctx.message.messageEntity.getChatId(), key.apply(ctx), value);
  }

  public TextJoiner get(String key) {
    return TextJoiner.text(ctx -> dataHelper.getFromScriptMap(ctx.message.messageEntity.getChatId(), key) != null
        ? dataHelper.getFromScriptMap(ctx.message.messageEntity.getChatId(), key).toString() : null);
  }

  public TextJoiner get(Function<ScriptContext, Object> key) {
    return TextJoiner.text(ctx -> dataHelper.getFromScriptMap(ctx.message.messageEntity.getChatId(), key.apply(ctx)) != null ?
        dataHelper.getFromScriptMap(ctx.message.messageEntity.getChatId(), key.apply(ctx)).toString() : null);
  }
}
