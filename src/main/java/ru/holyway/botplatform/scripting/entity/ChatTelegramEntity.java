package ru.holyway.botplatform.scripting.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatTitle;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.holyway.botplatform.scripting.ScriptContext;
import ru.holyway.botplatform.scripting.util.TextJoiner;

import java.util.function.Consumer;
import java.util.function.Function;

public class ChatTelegramEntity {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTelegramEntity.class);
  
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
            .execute(DeleteMessage.builder().chatId(getId().apply(scriptContext)).messageId(id).build());
      } catch (TelegramApiException e) {
        LOGGER.error("Error occurred during execution: ", e);
      }
    };
  }

  public Consumer<ScriptContext> delete(Function<ScriptContext, String> idFunc) {
    return scriptContext -> delete(Integer.parseInt(idFunc.apply(scriptContext))).accept(scriptContext);
  }

  public Consumer<ScriptContext> editTitle(Function<ScriptContext, String> title) {
    return scriptContext -> {
      try {
        scriptContext.message.messageEntity.getSender().execute(SetChatTitle.builder().chatId(getId().apply(scriptContext)).title(title.apply(scriptContext)).build());
      } catch (TelegramApiException e) {
        LOGGER.error("Error occurred during execution: ", e);
      }
    };
  }

  public Consumer<ScriptContext> editTitle(String title) {
    return scriptContext -> {
      try {
        scriptContext.message.messageEntity.getSender().execute(SetChatTitle.builder().chatId(getId().apply(scriptContext)).title(title).build());
      } catch (TelegramApiException e) {
        LOGGER.error("Error occurred during execution: ", e);
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
