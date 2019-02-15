package ru.holyway.botplatform.scripting.entity;

import java.util.function.Function;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

public class MessageScriptEntity extends AbstractTelegramEntity {

  public TelegramMessageEntity messageEntity;

  public AbstractText text = new AbstractText() {
    @Override
    public Function<ScriptContext, String> value() {
      return ctx -> ctx.message.messageEntity.getMessage().getText();
    }
  };

  public AbstractText user = new AbstractText() {
    @Override
    public Function<ScriptContext, String> value() {
      return ctx -> ctx.message.messageEntity.getMessage().getFrom().getUserName();
    }
  };

  public MessageScriptEntity() {
  }

  public MessageScriptEntity(TelegramMessageEntity messageEntity) {
    this.messageEntity = messageEntity;
  }

  @Override
  public Function<ScriptContext, Message> entity() {
    return scriptContext -> scriptContext.message.messageEntity.getMessage();
  }
}
