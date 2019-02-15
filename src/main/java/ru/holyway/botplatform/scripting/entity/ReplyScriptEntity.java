package ru.holyway.botplatform.scripting.entity;

import java.util.function.Function;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.holyway.botplatform.scripting.ScriptContext;

public class ReplyScriptEntity extends AbstractTelegramEntity {

  public AbstractText text = new AbstractText() {
    @Override
    public Function<ScriptContext, String> value() {
      return ctx -> entity().apply(ctx).getText();
    }
  };

  public AbstractText user = new AbstractText() {
    @Override
    public Function<ScriptContext, String> value() {
      return ctx -> entity().apply(ctx).getFrom().getUserName();
    }
  };

  @Override
  public Function<ScriptContext, Message> entity() {
    return scriptContext -> scriptContext.message.messageEntity.getMessage().getReplyToMessage();
  }
}
