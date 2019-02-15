package ru.holyway.botplatform.scripting.entity;

import java.util.function.Function;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.ScriptContext;

public class ForwardScriptEntity extends AbstractTelegramEntity {

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
    return ctx -> {
      try {
        return ctx.message.messageEntity.getSender().execute(
            new ForwardMessage().setChatId(ctx.message.messageEntity.getChatId())
                .setFromChatId(ctx.message.messageEntity.getChatId())
                .setMessageId(ctx.message.messageEntity.getMessage().getForwardFromMessageId()));
      } catch (TelegramApiException e) {
        e.printStackTrace();
        return null;
      }
    };
  }
}
