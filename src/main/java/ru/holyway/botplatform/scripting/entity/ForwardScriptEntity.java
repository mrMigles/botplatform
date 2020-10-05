package ru.holyway.botplatform.scripting.entity;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Function;

public class ForwardScriptEntity extends AbstractTelegramEntity {

  public AbstractText text = new AbstractText() {
    @Override
    public Function<ScriptContext, String> value() {
      return ctx -> entity().apply(ctx).getText();
    }
  };

  public AbstractText user = new UserScriptEntity(ctx -> entity().apply(ctx).getForwardFrom());

  @Override
  public Function<ScriptContext, Message> entity() {
    return ctx -> ctx.message.messageEntity.getMessage();
  }
}
