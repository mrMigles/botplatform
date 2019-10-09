package ru.holyway.botplatform.scripting.entity;

import java.util.function.Function;
import java.util.function.Predicate;
import ru.holyway.botplatform.scripting.ScriptContext;

public class StickerEntity extends AbstractText {

  @Override
  public Function<ScriptContext, String> value() {
    return ctx -> ctx.message.messageEntity.getMessage().getSticker().getFileId();
  }

  public Function<ScriptContext, String> getSet() {
    return ctx -> ctx.message.messageEntity.getMessage().getSticker().getSetName();
  }

  public Predicate<ScriptContext> emoji(String text) {
    return ctx -> ctx.message.messageEntity.getMessage().getSticker().getEmoji()
        .equalsIgnoreCase(text);
  }

  public Predicate<ScriptContext> fromSet(String text) {
    return ctx -> ctx.message.messageEntity.getMessage().getSticker().getSetName()
        .equalsIgnoreCase(text);
  }
}
