package ru.holyway.botplatform.scripting.entity;

import java.util.function.Function;
import ru.holyway.botplatform.scripting.ScriptContext;

public class TextScriptEntity extends AbstractText {


  public TextScriptEntity() {
  }

  public Function<ScriptContext, String> value() {
    return ctx -> ctx.message.messageEntity.getMessage().getText();
  }
}
