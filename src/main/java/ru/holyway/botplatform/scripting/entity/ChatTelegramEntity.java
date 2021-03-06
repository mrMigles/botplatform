package ru.holyway.botplatform.scripting.entity;

import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatTitle;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.TextJoiner;

import java.util.function.Consumer;
import java.util.function.Function;

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

  public Consumer<ScriptContext> editTitle(Function<ScriptContext, String> title) {
    return scriptContext -> {
      try {
        scriptContext.message.messageEntity.getSender().execute(new SetChatTitle().setChatId(getId().apply(scriptContext)).setTitle(title.apply(scriptContext)));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public Consumer<ScriptContext> editTitle(String title) {
    return scriptContext -> {
      try {
        scriptContext.message.messageEntity.getSender().execute(new SetChatTitle().setChatId(getId().apply(scriptContext)).setTitle(title));
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    };
  }

  public TextJoiner getId() {
    if (chatId == null) {
      return TextJoiner.text(scriptContext -> scriptContext.message.messageEntity.getChatId());
    }
    if (chatId instanceof Function) {
      return TextJoiner.text((Function<ScriptContext, Object>) chatId);
    } else {
      return TextJoiner.text(scriptContext -> chatId.toString());
    }
  }

}
