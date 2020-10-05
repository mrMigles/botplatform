package ru.holyway.botplatform.scripting.entity;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.TextJoiner;

import java.util.function.Function;

public class UserScriptEntity extends AbstractText {


  public UserScriptEntity() {
  }

  public UserScriptEntity(Function<ScriptContext, User> user) {
    this.user = user;
  }

  private Function<ScriptContext, User> user;

  public Function<ScriptContext, String> value() {
    return ctx -> entity().apply(ctx).getUserName();
  }

  public TextJoiner firstName() {
    return TextJoiner.text(ctx -> entity().apply(ctx).getFirstName());
  }

  public TextJoiner name() {
    return TextJoiner.text(ctx -> entity().apply(ctx).getFirstName() +
        (entity().apply(ctx).getLastName() != null ? " " + entity().apply(ctx).getLastName()
            : ""));
  }

  public TextJoiner id() {
    return TextJoiner.text(ctx -> entity().apply(ctx).getId().toString());
  }

  private Function<ScriptContext, User> entity() {
    return user != null ? user : ctx -> ctx.message.messageEntity.getMessage().getFrom();
  }
}
