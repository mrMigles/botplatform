package ru.holyway.botplatform.scripting;

import java.util.function.Function;
import java.util.function.Predicate;

public class TextScriptEntity {


  public TextScriptEntity() {
  }

  public Function<ScriptEntityContext, String> get() {
    return ctx -> ctx.message.messageEntity.getMessage().getText();
  }

  public Predicate<ScriptEntityContext> eq(String text) {
    return mes -> mes.message.messageEntity.getText().equals(text);
  }

  public Predicate<ScriptEntityContext> contains(String text) {
    return mes -> mes.message.messageEntity.getText().contains(text);
  }
}
