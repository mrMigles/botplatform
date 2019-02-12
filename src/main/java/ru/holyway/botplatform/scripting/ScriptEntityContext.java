package ru.holyway.botplatform.scripting;

public class ScriptEntityContext {

  public MessageScriptEntity message = new MessageScriptEntity();

  public TelegramScriptEntity telegram = new TelegramScriptEntity();

  public ScriptEntityContext() {
  }

  public ScriptEntityContext(MessageScriptEntity message,
      TelegramScriptEntity telegram) {
    this.message = message;
    this.telegram = telegram;
  }
}
