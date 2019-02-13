package ru.holyway.botplatform.scripting;

import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;

public class ScriptContext {

  public MessageScriptEntity message = new MessageScriptEntity();

  public TelegramScriptEntity telegram = new TelegramScriptEntity();

  public ScriptContext() {
  }

  public ScriptContext(MessageScriptEntity message, TelegramScriptEntity telegram) {
    this.message = message;
    this.telegram = telegram;
  }
}
