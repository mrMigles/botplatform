package ru.holyway.botplatform.scripting;

import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.entity.StaticMessage;
import ru.holyway.botplatform.telegram.TelegramMessageEntity;

import javax.annotation.Nonnull;

/**
 * Factory that encapsulates script context creation for different entry points
 * (inline execution or scheduled trigger).
 */
public class ScriptContextFactory {

  @Nonnull
  public ScriptContext create(@Nonnull TelegramMessageEntity messageEntity, @Nonnull Script script) {
    MessageScriptEntity message = new MessageScriptEntity(messageEntity);
    TelegramScriptEntity telegram = new TelegramScriptEntity();
    return new ScriptContext(message, telegram, script);
  }

  @Nonnull
  public ScriptContext create(@Nonnull String chat, @Nonnull AbsSender sender, @Nonnull Script script) {
    return create(new TelegramMessageEntity(new StaticMessage(chat), null, sender), script);
  }
}
