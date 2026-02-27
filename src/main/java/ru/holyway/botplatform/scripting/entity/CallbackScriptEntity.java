package ru.holyway.botplatform.scripting.entity;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.holyway.botplatform.scripting.ScriptContext;

import java.util.function.Function;

public class CallbackScriptEntity {

  private final Function<ScriptContext, CallbackQuery> callbackQuery = scriptContext -> scriptContext.message.messageEntity.getCallbackQuery();

  public AbstractText data = new AbstractText.Text(scriptContext -> callbackQuery.apply(scriptContext).getData());
  public AbstractText user = new AbstractText.Text(scriptContext -> callbackQuery.apply(scriptContext).getFrom().getUserName());
  public AbstractText id = new AbstractText.Text(scriptContext -> callbackQuery.apply(scriptContext).getId());

  public MessageScriptEntity getMessage() {
    return new MessageScriptEntity() {
      @Override
      public Function<ScriptContext, Message> entity() {
        return scriptContext -> (Message) scriptContext.message.messageEntity.getCallbackQuery().getMessage();
      }
    };
  }
}
