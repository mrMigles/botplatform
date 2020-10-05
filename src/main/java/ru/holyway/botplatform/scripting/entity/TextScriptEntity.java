package ru.holyway.botplatform.scripting.entity;

import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Function;

public class TextScriptEntity extends AbstractText {


  public TextScriptEntity() {
  }

  public Function<ScriptContext, String> value() {
    return ctx -> ctx.message.messageEntity.getMessage().getText();
  }
}
