package ru.holyway.botplatform.scripting.entity;

import java.util.function.Consumer;
import java.util.function.Function;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.ScriptContext;

public class ChatTelegramEntity {

  private final Object chatId;

  public ChatTelegramEntity(Object chatId) {
    this.chatId = chatId;
  }

  public static ChatTelegramEntity chat() {
    return new ChatTelegramEntity(null);
  }

  public static ChatTelegramEntity chat(long id) {
    return new ChatTelegramEntity(id);
  }

  public static ChatTelegramEntity chat(Function<ScriptContext, String> idFunc) {
    return new ChatTelegramEntity(idFunc);
  }

  public Consumer<ScriptContext> delete(final int id) {
    return scriptContext -> {
      try {
        scriptContext.message.messageEntity.getSender()
            .execute(new DeleteMessage().setChatId(getId().apply(scriptContext)).setMessageId(id));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> delete(Function<ScriptContext, String> idFunc) {
    return scriptContext -> delete(Integer.parseInt(idFunc.apply(scriptContext))).accept(scriptContext);
  }

  public Function<ScriptContext, String> getId() {
    if (chatId == null){
      return scriptContext -> scriptContext.message.messageEntity.getChatId();
    }
    if (chatId instanceof Function) {
      return (Function<ScriptContext, String>) chatId;
    } else {
      return scriptContext -> chatId.toString();
    }
  }

}
