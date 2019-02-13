package ru.holyway.botplatform.scripting.entity;

import java.util.function.Function;
import java.util.function.Predicate;
import ru.holyway.botplatform.scripting.ScriptContext;

public class TextScriptEntity {


  public TextScriptEntity() {
  }

  public Function<ScriptContext, String> value() {
    return ctx -> ctx.message.messageEntity.getMessage().getText();
  }

  public Predicate<ScriptContext> eq(String text) {
    return mes -> mes.message.messageEntity.getText().equals(text);
  }

  public Predicate<ScriptContext> contains(String text) {
    return mes -> mes.message.messageEntity.getText().contains(text);
  }
}
