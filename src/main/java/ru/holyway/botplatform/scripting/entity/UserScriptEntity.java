package ru.holyway.botplatform.scripting.entity;

import java.util.function.Function;
import ru.holyway.botplatform.scripting.ScriptContext;

public class UserScriptEntity extends AbstractText {


  public UserScriptEntity() {
  }

  public Function<ScriptContext, String> value() {
    return ctx -> ctx.message.messageEntity.getMessage().getFrom().getUserName();
  }
}
