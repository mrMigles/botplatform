package ru.holyway.botplatform.scripting;

import ru.holyway.botplatform.scripting.entity.ForwardScriptEntity;
import ru.holyway.botplatform.scripting.entity.MessageScriptEntity;
import ru.holyway.botplatform.scripting.entity.ReplyScriptEntity;

public class ScriptContext {

  public MessageScriptEntity message = new MessageScriptEntity();

  public TelegramScriptEntity telegram = new TelegramScriptEntity();

  public ReplyScriptEntity reply = new ReplyScriptEntity();

  ForwardScriptEntity forward = new ForwardScriptEntity();

  public ScriptContext() {
  }

  public ScriptContext(MessageScriptEntity message, TelegramScriptEntity telegram) {
    this.message = message;
    this.telegram = telegram;
  }
}
