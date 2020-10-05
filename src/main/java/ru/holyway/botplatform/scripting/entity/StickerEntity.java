package ru.holyway.botplatform.scripting.entity;

import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.TextJoiner;

import java.util.function.Function;
import java.util.function.Predicate;

public class StickerEntity extends AbstractText {

  @Override
  public Function<ScriptContext, String> value() {
    return ctx -> ctx.message.messageEntity.getMessage().getSticker().getFileId();
  }

  public TextJoiner getSet() {
    return TextJoiner.text(ctx -> ctx.message.messageEntity.getMessage().getSticker().getSetName());
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
