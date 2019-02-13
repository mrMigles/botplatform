package ru.holyway.botplatform.scripting.entity;

import java.util.function.Function;
import java.util.function.Predicate;
import ru.holyway.botplatform.scripting.ScriptContext;

public class UserScriptEntity {


  public UserScriptEntity() {
  }

  public Function<ScriptContext, String> value() {
    return ctx -> ctx.message.messageEntity.getMessage().getFrom().getUserName();
  }

  public Predicate<ScriptContext> eq(String userName) {
    return mes -> mes.message.messageEntity.getMessage().getFrom().getUserName().equals(userName);
  }

  public Predicate<ScriptContext> contains(String userName) {
    return mes -> mes.message.messageEntity.getMessage().getFrom().getUserName().contains(userName);
  }

}
