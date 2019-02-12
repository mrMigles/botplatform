package ru.holyway.botplatform.scripting;

import java.util.function.Function;
import java.util.function.Predicate;

public class UserScriptEntity {


  public UserScriptEntity() {
  }

  public Function<ScriptEntityContext, String> get() {
    return ctx -> ctx.message.messageEntity.getMessage().getFrom().getUserName();
  }

  public Predicate<ScriptEntityContext> eq(String userName) {
    return mes -> mes.message.messageEntity.getMessage().getFrom().getUserName().equals(userName);
  }

  public Predicate<ScriptEntityContext> contains(String userName) {
    return mes -> mes.message.messageEntity.getMessage().getFrom().getUserName().contains(userName);
  }

}
